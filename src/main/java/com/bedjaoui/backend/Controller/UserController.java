package com.bedjaoui.backend.Controller;

import com.bedjaoui.backend.DTO.UserDTO;
import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Model.User.User;
import com.bedjaoui.backend.Service.SoundService;
import com.bedjaoui.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<User> addUser(@RequestBody User user) {
        if (userService.checkIfUserExists(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        User savedUser = userService.addUser(user);
        return ResponseEntity.ok(savedUser);
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


}

