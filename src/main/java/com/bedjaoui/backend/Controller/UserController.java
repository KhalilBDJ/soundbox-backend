package com.bedjaoui.backend.Controller;

import com.bedjaoui.backend.DTO.UserDTO;
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
    @PostMapping("/register")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        if (userService.checkIfUserExists(userDTO.getEmail())) {
            return ResponseEntity.badRequest().build(); // Utilisateur existe déjà
        }
        UserDTO savedUserDTO = userService.addUser(userDTO);
        return ResponseEntity.ok(savedUserDTO);
    }

    // Récupérer tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userService.getAllUsers();
        return ResponseEntity.ok(userDTOs);
    }

    // Récupérer un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<UserDTO> userDTO = userService.getUserById(id);
        return userDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Récupérer un utilisateur par email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        Optional<UserDTO> userDTO = userService.getUserByEmail(email);
        return userDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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

