package br.edu.idp.stsw.testpyramid.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserService.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@DisplayName("Integration Tests - UserService with Repository")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TestEntityManager entityManager;

    // ========== INTEGRATION TESTS - SERVICE + REPOSITORY ==========

    @Test
    @DisplayName("Should create and retrieve user through service")
    void shouldCreateAndRetrieveUserThroughService() {
        // Given
        String name = "Integration Test User";
        String email = "integration@example.com";
        String password = "password123";

        // When
        User created = userService.createUser(name, email, password);

        // Then
        assertNotNull(created.getId());
        assertEquals(name, created.getName());
        assertEquals(email, created.getEmail());
        assertTrue(created.isActive());

        // Verify persistence
        User found = userService.findUserById(created.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("Should find user by email through service")
    void shouldFindUserByEmailThroughService() {
        // Given
        User user = new User("Email Test User", "emailtest@example.com", "password123");
        entityManager.persistAndFlush(user);

        // When
        User found = userService.findUserByEmail("emailtest@example.com").orElse(null);

        // Then
        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
        assertEquals("Email Test User", found.getName());
    }

    @Test
    @DisplayName("Should list all active users")
    void shouldListAllActiveUsers() {
        // Given
        User activeUser1 = new User("Active User 1", "active1@example.com", "password123");
        User activeUser2 = new User("Active User 2", "active2@example.com", "password123");
        User inactiveUser = new User("Inactive User", "inactive@example.com", "password123");
        inactiveUser.setActive(false);

        entityManager.persistAndFlush(activeUser1);
        entityManager.persistAndFlush(activeUser2);
        entityManager.persistAndFlush(inactiveUser);

        // When
        var activeUsers = userService.findAllActiveUsers();

        // Then
        assertEquals(2, activeUsers.size());
        assertTrue(activeUsers.stream().allMatch(User::isActive));
        assertTrue(activeUsers.stream().anyMatch(u -> u.getName().equals("Active User 1")));
        assertTrue(activeUsers.stream().anyMatch(u -> u.getName().equals("Active User 2")));
    }

    @Test
    @DisplayName("Should update user through service")
    void shouldUpdateUserThroughService() {
        // Given
        User user = new User("Original User", "original@example.com", "password123");
        entityManager.persistAndFlush(user);

        String newName = "Updated User";
        String newEmail = "updated@example.com";

        // When
        User updated = userService.updateUser(user.getId(), newName, newEmail);

        // Then
        assertEquals(newName, updated.getName());
        assertEquals(newEmail, updated.getEmail());

        // Verify persistence
        User found = userService.findUserById(user.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newEmail, found.getEmail());
    }

    @Test
    @DisplayName("Should deactivate user through service")
    void shouldDeactivateUserThroughService() {
        // Given
        User user = new User("User to Deactivate", "deactivate@example.com", "password123");
        entityManager.persistAndFlush(user);

        // When
        userService.deactivateUser(user.getId());

        // Then
        User found = userService.findUserById(user.getId()).orElse(null);
        assertNotNull(found);
        assertFalse(found.isActive());
    }

    @Test
    @DisplayName("Should delete user through service")
    void shouldDeleteUserThroughService() {
        // Given
        User user = new User("User to Delete", "delete@example.com", "password123");
        entityManager.persistAndFlush(user);

        // When
        userService.deleteUser(user.getId());

        // Then
        assertFalse(userService.findUserById(user.getId()).isPresent());
    }

    @Test
    @DisplayName("Should prevent duplicate emails during creation")
    void shouldPreventDuplicateEmailsDuringCreation() {
        // Given
        userService.createUser("First User", "duplicate@example.com", "password123");

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Second User", "duplicate@example.com", "password456"));
    }

    @Test
    @DisplayName("Should prevent duplicate emails during update")
    void shouldPreventDuplicateEmailsDuringUpdate() {
        // Given
        User user1 = userService.createUser("User 1", "user1@example.com", "password123");
        userService.createUser("User 2", "user2@example.com", "password123");

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(user1.getId(), "Updated User 1", "user2@example.com"));
    }
}
