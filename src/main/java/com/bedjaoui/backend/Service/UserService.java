package com.bedjaoui.backend.Service;

import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Model.User;
import com.bedjaoui.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Ajouter un nouvel utilisateur avec un mot de passe haché
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Récupérer tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Récupérer un utilisateur par ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Récupérer un utilisateur par email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Vérifier si un utilisateur existe par email
    public boolean checkIfUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Vérifier si un utilisateur existe par ID
    public boolean checkIfUserExists(Long userId) {
        return userRepository.findById(userId).isPresent();
    }

    // Vérifier si un utilisateur existe par email et mot de passe
    public boolean checkIfUserExists(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}
