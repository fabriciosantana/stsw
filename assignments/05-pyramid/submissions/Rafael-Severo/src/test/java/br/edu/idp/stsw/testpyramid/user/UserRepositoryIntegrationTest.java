package br.edu.idp.stsw.testpyramid.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@DisplayName("Integration Tests - UserRepository")
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    // ========== INTEGRATION TESTS - MIDDLE OF THE PYRAMID ==========

    @Test
    @DisplayName("Should save and retrieve user")
    void shouldSaveAndRetrieveUser() {
        // Given
        User user = new User("John Doe", "john@example.com", "password123");
        entityManager.persistAndFlush(user);

        // When
        User found = userRepository.findById(user.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals(user.getName(), found.getName());
        assertEquals(user.getEmail(), found.getEmail());
        assertTrue(found.isActive());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        User user = new User("Jane Doe", "jane@example.com", "password123");
        entityManager.persistAndFlush(user);

        // When
        User found = userRepository.findByEmail("jane@example.com").orElse(null);

        // Then
        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
        assertEquals("Jane Doe", found.getName());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void shouldReturnEmptyWhenEmailNotFound() {
        // When
        var result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // Given
        User user = new User("Test User", "test@example.com", "password123");
        entityManager.persistAndFlush(user);

        // When & Then
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("other@example.com"));
    }

    @Test
    @DisplayName("Should find only active users")
    void shouldFindOnlyActiveUsers() {
        // Given
        User activeUser = new User("Active User", "active@example.com", "password123");
        activeUser.setActive(true);

        User inactiveUser = new User("Inactive User", "inactive@example.com", "password123");
        inactiveUser.setActive(false);

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(inactiveUser);

        // When
        List<User> activeUsers = userRepository.findByActiveTrue();

        // Then
        assertEquals(1, activeUsers.size());
        assertEquals("Active User", activeUsers.get(0).getName());
        assertTrue(activeUsers.get(0).isActive());
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() {
        // Given
        User user = new User("Original Name", "original@example.com", "password123");
        entityManager.persistAndFlush(user);

        // When
        user.setName("Updated Name");
        user.setEmail("updated@example.com");
        User updated = entityManager.merge(user);
        entityManager.flush();

        User found = userRepository.findById(user.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals("Updated Name", found.getName());
        assertEquals("updated@example.com", found.getEmail());
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // Given
        User user = new User("User to Delete", "delete@example.com", "password123");
        entityManager.persistAndFlush(user);

        // When
        userRepository.deleteById(user.getId());

        // Then
        assertFalse(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    @DisplayName("Should handle multiple users")
    void shouldHandleMultipleUsers() {
        // Given
        User user1 = new User("User 1", "user1@example.com", "password123");
        User user2 = new User("User 2", "user2@example.com", "password123");
        User user3 = new User("User 3", "user3@example.com", "password123");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        // When
        List<User> allUsers = userRepository.findAll();

        // Then
        assertEquals(3, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user1@example.com")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user3@example.com")));
    }
}
