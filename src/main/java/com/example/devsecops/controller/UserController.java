package com.example.devsecops.controller;

import com.example.devsecops.model.User;
import com.example.devsecops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Récupérer la liste de tous les utilisateurs
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Ajouter un nouvel utilisateur
    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);  // <-- ici c'est save() et non saveUser()
    }
}
