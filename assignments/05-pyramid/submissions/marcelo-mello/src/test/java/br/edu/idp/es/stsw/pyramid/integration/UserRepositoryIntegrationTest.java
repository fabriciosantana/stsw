package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.user.User;
import br.edu.idp.es.stsw.pyramid.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CAMADA 2 DA PIRÂMIDE DE TESTES: TESTES DE INTEGRAÇÃO (NÍVEL INTERMEDIÁRIO)
 * 
 * Características:
 * - Testam a integração real da aplicação com a camada de persistência (Banco de Dados H2 em memória).
 * - Utilizam @DataJpaTest para carregar apenas a infraestrutura JPA/Hibernate.
 * - Validam mapeamento ORM, queries JPA e restrições de integridade (ex: chave única de email).
 */
@DataJpaTest
@DisplayName("Nível 2 - Teste de Integração: UserRepository com Banco H2")
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User("Carlos Eduardo", "carlos@example.com", "ADMIN");
        user2 = new User("Beatriz Lima", "beatriz@example.com", "USER");
    }

    @Test
    @DisplayName("Deve salvar e recuperar usuário do banco de dados")
    void shouldPersistAndRetrieveUser() {
        // Act
        User savedUser = entityManager.persistFlushFind(user1);

        // Assert
        assertThat(savedUser.getId()).isNotNull();
        Optional<User> found = userRepository.findById(savedUser.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("carlos@example.com");
    }

    @Test
    @DisplayName("Deve buscar usuário por email existente")
    void shouldFindByEmail() {
        // Arrange
        entityManager.persistAndFlush(user1);

        // Act
        Optional<User> found = userRepository.findByEmail("carlos@example.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Carlos Eduardo");
    }

    @Test
    @DisplayName("Deve verificar se email existe no banco")
    void shouldCheckExistsByEmail() {
        // Arrange
        entityManager.persistAndFlush(user1);

        // Act & Assert
        assertThat(userRepository.existsByEmail("carlos@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("inexistente@example.com")).isFalse();
    }

    @Test
    @DisplayName("Deve listar usuários filtrando por status ativo")
    void shouldFindByActiveStatus() {
        // Arrange
        user2.setActive(false);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // Act
        List<User> activeUsers = userRepository.findByActive(true);
        List<User> inactiveUsers = userRepository.findByActive(false);

        // Assert
        assertThat(activeUsers).hasSize(1).extracting(User::getName).containsExactly("Carlos Eduardo");
        assertThat(inactiveUsers).hasSize(1).extracting(User::getName).containsExactly("Beatriz Lima");
    }
}
