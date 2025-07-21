package com.example.devsecops.service;

import com.example.devsecops.model.User;
import com.example.devsecops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void shouldReturnAllUsers() {
        User user1 = new User(1L, "alice", "alice@example.com");
        User user2 = new User(2L, "bob", "bob@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getUsername()).isEqualTo("alice");
    }

    @Test
    void shouldSaveUser() {
        User user = new User(null, "charlie", "charlie@example.com");
        when(userRepository.save(user)).thenReturn(new User(3L, "charlie", "charlie@example.com"));

        User savedUser = userService.saveUser(user);

        assertThat(savedUser.getId()).isEqualTo(3L);
        verify(userRepository, times(1)).save(user);
    }
}
