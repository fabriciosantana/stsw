package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.user.User;
import br.edu.idp.es.stsw.pyramid.user.UserRepository;
import br.edu.idp.es.stsw.pyramid.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CAMADA 2 DA PIRÂMIDE DE TESTES: TESTES DE INTEGRAÇÃO DE SERVIÇO (SPRING CONTEXT)
 * 
 * Características:
 * - Carrega o contexto Spring real completo e conecta o UserService ao UserRepository no H2.
 * - Garante que as transações (@Transactional) funcionam e os dados são persistidos/alterados de fato no banco.
 */
@SpringBootTest
@Transactional
@DisplayName("Nível 2 - Teste de Integração: UserService + Spring Container + H2")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar usuário no banco de dados através da camada de serviço")
    void shouldCreateAndPersistUserInDatabase() {
        // Arrange
        User newUser = new User("Fernanda Souza", "fernanda@example.com", "MANAGER");

        // Act
        User created = userService.createUser(newUser);

        // Assert
        assertThat(created.getId()).isNotNull();
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.findById(created.getId())).isPresent();
    }

    @Test
    @DisplayName("Deve rejeitar criação de dois usuários com o mesmo email na integração com o banco")
    void shouldRejectDuplicateEmailInRealDatabase() {
        // Arrange
        User user1 = new User("User Um", "duplicado@example.com", "USER");
        User user2 = new User("User Dois", "duplicado@example.com", "USER");
        userService.createUser(user1);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(user2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email já cadastrado");

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve atualizar dados do usuário e persistir alterações no banco")
    void shouldUpdateUserInDatabase() {
        // Arrange
        User user = userService.createUser(new User("Nome Antigo", "antigo@example.com", "USER"));

        // Act
        User updateData = new User("Nome Novo", "novo@example.com", "ADMIN");
        User updated = userService.updateUser(user.getId(), updateData);

        // Assert
        assertThat(updated.getName()).isEqualTo("Nome Novo");
        assertThat(updated.getEmail()).isEqualTo("novo@example.com");
        assertThat(updated.getRole()).isEqualTo("ADMIN");

        User persisted = userRepository.findById(user.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("Nome Novo");
    }

    @Test
    @DisplayName("Deve desativar e alterar o estado do usuário no banco")
    void shouldDeactivateUserInDatabase() {
        // Arrange
        User user = userService.createUser(new User("Lucas Lima", "lucas@example.com", "USER"));

        // Act
        userService.deactivateUser(user.getId());

        // Assert
        User persisted = userRepository.findById(user.getId()).orElseThrow();
        assertThat(persisted.isActive()).isFalse();
    }
}
