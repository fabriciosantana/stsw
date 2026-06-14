package br.edu.idp.stsw.testpyramid.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ========== UNIT TESTS - BASE OF THE PYRAMID ==========

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        String name = "John Doe";
        String email = "john@example.com";
        String password = "password123";

        User savedUser = new User(name, email, password);
        savedUser.setId(1L);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.createUser(name, email, password);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(any(User.class));
    }

    @ParameterizedTest(name = "Should reject invalid name: {0}")
    @CsvSource({
            "'', Name cannot be null or empty",
            "'A', Name must be between 3 and 100 characters",
            "'AB', Name must be between 3 and 100 characters"
    })
    @DisplayName("Should reject invalid names")
    void shouldRejectInvalidNames(String invalidName, String expectedMessage) {
        // Given
        String email = "test@example.com";
        String password = "password123";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(invalidName, email, password));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest(name = "Should reject invalid email: {0}")
    @CsvSource({
            "'', Email cannot be null or empty",
            "'invalid-email', Invalid email format",
            "'user@', Invalid email format",
            "'user.com', Invalid email format"
    })
    @DisplayName("Should reject invalid emails")
    void shouldRejectInvalidEmails(String invalidEmail, String expectedMessage) {
        // Given
        String name = "John Doe";
        String password = "password123";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(name, invalidEmail, password));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should reject duplicate email")
    void shouldRejectDuplicateEmail() {
        // Given
        String name = "John Doe";
        String email = "john@example.com";
        String password = "password123";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(name, email, password));
        assertEquals("Email already exists: " + email, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Given
        Long userId = 1L;
        User user = new User("John Doe", "john@example.com", "password123");
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findUserById(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void shouldReturnEmptyWhenUserNotFoundById() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findUserById(userId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        Long userId = 1L;
        User existingUser = new User("John Doe", "john@example.com", "password123");
        existingUser.setId(userId);

        String newName = "Jane Doe";
        String newEmail = "jane@example.com";

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        User result = userService.updateUser(userId, newName, newEmail);

        // Then
        assertEquals(newName, result.getName());
        assertEquals(newEmail, result.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(userId, "New Name", "new@example.com"));
        assertEquals("User not found: " + userId, exception.getMessage());
    }

    @Test
    @DisplayName("Should deactivate user")
    void shouldDeactivateUser() {
        // Given
        Long userId = 1L;
        User user = new User("John Doe", "john@example.com", "password123");
        user.setId(userId);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.deactivateUser(userId);

        // Then
        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(userId));
        assertEquals("User not found: " + userId, exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }
}
