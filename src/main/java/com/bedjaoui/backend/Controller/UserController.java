package com.bedjaoui.backend.Controller;

import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Model.User;
import com.bedjaoui.backend.Repository.SoundRepository;
import com.bedjaoui.backend.Repository.UserRepository;
import com.bedjaoui.backend.Service.AuthService;
import com.bedjaoui.backend.Service.SoundService;
import com.bedjaoui.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.expression.ExpressionException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final SoundService soundService;

    @Autowired
    public UserController(UserService userService, SoundService soundService) {
        this.userService = userService;
        this.soundService = soundService;
    }

    // Création d'un nouvel utilisateur
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        if (userService.checkIfUserExists(user.getEmail())) {
            return ResponseEntity.badRequest().build(); // Utilisateur existe déjà
        }
        User savedUser = userService.addUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // Récupérer tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Récupérer un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Récupérer un utilisateur par email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Récupérer tous les sons d'un utilisateur
    @GetMapping("/{userId}/sounds")
    public ResponseEntity<List<Sound>> getUserSounds(@PathVariable Long userId) {
        if (!userService.checkIfUserExists(userId)) {
            return ResponseEntity.notFound().build();
        }
        List<Sound> sounds = soundService.getSoundsByUserId(userId).orElseThrow(() -> new IllegalArgumentException("No user found or list of sound is empty"));
        return ResponseEntity.ok(sounds);
    }
}

