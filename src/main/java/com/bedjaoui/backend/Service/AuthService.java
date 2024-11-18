package com.bedjaoui.backend.Service;


import com.bedjaoui.backend.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Méthode pour authentifier un utilisateur
    public Optional<User> authenticate(String email, String password) {
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    // Méthode pour inscrire un utilisateur
    public User register(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        return userService.addUser(user);
    }

    // Méthode pour générer un JWT
    public String generateJwtToken(User user) {
        // Ici, on simule un token pour l'exemple, tu pourrais utiliser une vraie implémentation JWT
        return "fake-jwt-token-for-user-" + user.getId();
    }
}