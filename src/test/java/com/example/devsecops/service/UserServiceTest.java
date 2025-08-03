package com.example.devsecops.service;

import com.example.devsecops.model.User;
import com.example.devsecops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        List<User> mockUsers = Arrays.asList(
            new User(1L, "Alice", "alice@example.com"),
            new User(2L, "Bob", "bob@example.com")
        );

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("Alice", users.get(0).getName());
    }

    @Test
    void testGetUserById_UserExists() {
        User user = new User(1L, "Alice", "alice@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Alice", result.getName());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.getUserById(99L);

        assertNull(result);
    }

    @Test
    void testSaveUser() {
        User user = new User(null, "Charlie", "charlie@example.com");
        User savedUser = new User(3L, "Charlie", "charlie@example.com");

        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.saveUser(user);

        assertEquals(3L, result.getId());
        assertEquals("Charlie", result.getName());
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
