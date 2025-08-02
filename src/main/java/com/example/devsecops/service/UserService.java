package com.example.devsecops.service;

import com.example.devsecops.model.User;
import com.example.devsecops.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructeur pour injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Méthode pour récupérer tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Méthode pour sauvegarder un utilisateur
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Tu peux ajouter d'autres méthodes si besoin
}
