package br.edu.stsw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Docker indisponível causa falha; nenhum teste é ignorado.
@Testcontainers(disabledWithoutDocker = false)
class IntegracaoTest {
    // Campo de instância: a extensão cria e remove um PostgreSQL exclusivo por teste.
    @Container
    private final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16.4-alpine")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
            // O módulo Java desliga fsync por padrão; preservamos a configuração Rust.
            .withCommand("postgres", "-c", "fsync=on");

    private Connection conexao;
    private Repositorio repositorio;

    @BeforeEach
    void prepararBanco(TestInfo teste) throws Exception {
        // A extensão já iniciou o contêiner e aguardou a prontidão do PostgreSQL.
        System.out.printf("PostgreSQL do teste %s: %s:%d (contêiner %s)%n",
                teste.getDisplayName(), postgres.getHost(), postgres.getMappedPort(5432),
                postgres.getContainerId());
        // getJdbcUrl usa o host e a porta escolhidos pelo runtime, sem fixar a porta do host.
        conexao = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        // Mostra a configuração efetiva do banco usado na demonstração.
        try (var consulta = conexao.createStatement();
                var resultado = consulta.executeQuery("SHOW fsync")) {
            resultado.next();
            System.out.printf("fsync do PostgreSQL: %s%n", resultado.getString(1));
        }
        repositorio = new Repositorio(conexao);
        repositorio.preparar();
    }

    @AfterEach
    void fecharConexao() throws SQLException {
        if (conexao != null) {
            conexao.close();
        }
        // Depois deste método, a extensão encerra e remove o contêiner do teste.
    }

    @Test
    void cadastroPersisteEPodeSerConsultado() throws SQLException {
        // assertEquals falha se o resultado real for diferente do esperado.
        assertEquals(0, repositorio.total());
        int id = repositorio.cadastrar("ana@example.com");

        assertEquals(Optional.of("ana@example.com"), repositorio.buscar(id));
        assertEquals(Optional.empty(), repositorio.buscar(-1));
        assertEquals(1, repositorio.total());
    }

    @Test
    void emailDuplicadoERejeitadoPeloPostgresql() throws SQLException {
        repositorio.cadastrar("ana@example.com");

        // assertThrows exige a rejeição; SQLSTATE 23505 identifica a violação de UNIQUE.
        var erro = assertThrows(SQLException.class, () -> repositorio.cadastrar("ana@example.com"));
        assertEquals("23505", erro.getSQLState());
        assertEquals(1, repositorio.total());
    }

    @Test
    void loteComDuplicidadeEDesfeitoPorInteiro() throws SQLException {
        int id = repositorio.cadastrar("ana@example.com");

        // Bia entra no lote; repetir Ana provoca rollback de todo o lote.
        var erro = assertThrows(SQLException.class,
                () -> repositorio.cadastrarLote(List.of("bia@example.com", "ana@example.com")));
        assertEquals("23505", erro.getSQLState());
        assertEquals(1, repositorio.total());
        assertEquals(Optional.of("ana@example.com"), repositorio.buscar(id));
    }
}
