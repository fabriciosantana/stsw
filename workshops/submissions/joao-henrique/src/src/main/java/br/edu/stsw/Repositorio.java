package br.edu.stsw;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

// Recebe uma conexão JDBC pronta; quem a abriu também deve fechá-la.
public final class Repositorio {
    private final Connection conexao;

    public Repositorio(Connection conexao) {
        this.conexao = conexao;
    }

    public void preparar() throws IOException, SQLException {
        // O Maven inclui schema.sql nos recursos da aplicação.
        try (var recurso = Repositorio.class.getResourceAsStream("/schema.sql")) {
            if (recurso == null) {
                throw new IOException("Recurso schema.sql não encontrado.");
            }
            var sql = new String(recurso.readAllBytes(), StandardCharsets.UTF_8);
            try (var consulta = conexao.createStatement()) {
                consulta.execute(sql);
            }
        }
    }

    public int cadastrar(String email) throws SQLException {
        // O parâmetro ? recebe o e-mail separado do SQL; o banco gera o ID.
        try (var consulta = conexao.prepareStatement(
                "INSERT INTO usuarios (email) VALUES (?) RETURNING id")) {
            consulta.setString(1, email);
            try (var resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    throw new SQLException("O cadastro não retornou um ID.");
                }
                return resultado.getInt("id");
            }
        }
    }

    public Optional<String> buscar(int id) throws SQLException {
        try (var consulta = conexao.prepareStatement("SELECT email FROM usuarios WHERE id = ?")) {
            consulta.setInt(1, id);
            // try-with-resources fecha consultas e resultados, inclusive se houver erro.
            try (var resultado = consulta.executeQuery()) {
                return resultado.next() ? Optional.of(resultado.getString("email")) : Optional.empty();
            }
        }
    }

    public long total() throws SQLException {
        try (var consulta = conexao.prepareStatement("SELECT COUNT(*) FROM usuarios");
                var resultado = consulta.executeQuery()) {
            if (!resultado.next()) {
                throw new SQLException("A contagem não retornou um resultado.");
            }
            return resultado.getLong(1);
        }
    }

    public void cadastrarLote(List<String> emails) throws SQLException {
        // Sem autocommit, o lote inteiro só é confirmado depois da última inserção.
        conexao.setAutoCommit(false);
        try {
            try (var consulta = conexao.prepareStatement("INSERT INTO usuarios (email) VALUES (?)")) {
                for (var email : emails) {
                    consulta.setString(1, email);
                    consulta.executeUpdate();
                }
            }
            conexao.commit();
        } catch (SQLException | RuntimeException erro) {
            // Desfaz inclusive as inserções anteriores ao erro, sem apagar o erro original.
            try {
                conexao.rollback();
                conexao.setAutoCommit(true);
            } catch (SQLException erroRollback) {
                erro.addSuppressed(erroRollback);
            }
            throw erro;
        }
        conexao.setAutoCommit(true);
    }
}
