package br.edu.idp.es.stsw.pyramid.unit;

import br.edu.idp.es.stsw.pyramid.user.User;
import br.edu.idp.es.stsw.pyramid.user.UserRepository;
import br.edu.idp.es.stsw.pyramid.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * CAMADA 1 DA PIRÂMIDE DE TESTES: TESTES UNITÁRIOS (NÍVEL DE BASE)
 * 
 * Características:
 * - Execução extremamente rápida (isolada em memória).
 * - Utiliza Mockito para isolar colaboradores externos (UserRepository).
 * - Foco total na lógica de negócio e regras de domínio da UserService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Nível 1 - Testes Unitários: UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Marcelo Mello", "marcelo@example.com", "ADMIN", true);
    }

    @Nested
    @DisplayName("Criação de Usuários")
    class CreateUserTests {

        @Test
        @DisplayName("Deve criar usuário com sucesso quando dados forem válidos")
        void shouldCreateUserSuccessfully() {
            // Arrange
            when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);

            // Act
            User created = userService.createUser(sampleUser);

            // Assert
            assertThat(created).isNotNull();
            assertThat(created.getId()).isEqualTo(1L);
            assertThat(created.getName()).isEqualTo("Marcelo Mello");
            verify(userRepository).existsByEmail(sampleUser.getEmail());
            verify(userRepository).save(sampleUser);
        }

        @Test
        @DisplayName("Deve atribuir role padrão USER se role for nula ou vazia")
        void shouldDefaultToUserRoleWhenEmpty() {
            // Arrange
            User noRoleUser = new User("Ana Silva", "ana@example.com", null);
            when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            User created = userService.createUser(noRoleUser);

            // Assert
            assertThat(created.getRole()).isEqualTo("USER");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar cadastrar email já existente")
        void shouldThrowExceptionWhenEmailExists() {
            // Arrange
            when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(sampleUser))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email já cadastrado");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando email for nulo ou vazio")
        void shouldThrowExceptionWhenEmailNullOrEmpty() {
            // Arrange
            User invalidUser = new User("Pedro", "", "USER");

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(invalidUser))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email não pode ser nulo ou vazio");
        }
    }

    @Nested
    @DisplayName("Consulta de Usuários")
    class FindUserTests {

        @Test
        @DisplayName("Deve retornar usuário por ID existente")
        void shouldGetUserById() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

            // Act
            User found = userService.getUserById(1L);

            // Assert
            assertThat(found).isEqualTo(sampleUser);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar ID inexistente")
        void shouldThrowWhenIdNotFound() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Usuário não encontrado com ID: 99");
        }

        @Test
        @DisplayName("Deve listar todos os usuários")
        void shouldGetAllUsers() {
            // Arrange
            when(userRepository.findAll()).thenReturn(List.of(sampleUser));

            // Act
            List<User> users = userService.getAllUsers();

            // Assert
            assertThat(users).hasSize(1).contains(sampleUser);
        }
    }

    @Nested
    @DisplayName("Atualização e Desativação")
    class UpdateAndDeleteTests {

        @Test
        @DisplayName("Deve desativar usuário com sucesso")
        void shouldDeactivateUser() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

            // Act
            userService.deactivateUser(1L);

            // Assert
            assertThat(sampleUser.isActive()).isFalse();
            verify(userRepository).save(sampleUser);
        }

        @Test
        @DisplayName("Deve deletar usuário por ID existente")
        void shouldDeleteUser() {
            // Arrange
            when(userRepository.existsById(1L)).thenReturn(true);

            // Act
            userService.deleteUser(1L);

            // Assert
            verify(userRepository).deleteById(1L);
        }
    }
}
