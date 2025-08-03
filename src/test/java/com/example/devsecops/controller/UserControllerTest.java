package com.example.devsecops.controller;

import com.example.devsecops.model.User;
import com.example.devsecops.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Test
    void testGetAllUsers() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController(userService);

        List<User> users = Arrays.asList(new User(1L, "Alice", "alice@example.com"));
        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = controller.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
    }

    @Test
    void testGetUserById() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController(userService);

        User user = new User(1L, "Bob", "bob@example.com");
        when(userService.getUserById(1L)).thenReturn(user);

        User result = controller.getUserById(1L);

        assertNotNull(result);
        assertEquals("Bob", result.getName());
    }

    @Test
    void testCreateUser() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController(userService);

        User user = new User(null, "Charlie", "charlie@example.com");
        User savedUser = new User(2L, "Charlie", "charlie@example.com");

        when(userService.saveUser(user)).thenReturn(savedUser);

        User result = controller.createUser(user);

        assertEquals(2L, result.getId());
    }

    @Test
    void testDeleteUser() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController(userService);

        controller.deleteUser(1L);

        verify(userService, times(1)).deleteUser(1L);
    }
}
