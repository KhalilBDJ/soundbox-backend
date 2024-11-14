package com.bedjaoui.backend.Controller;

import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Model.User;
import com.bedjaoui.backend.Repository.SoundRepository;
import com.bedjaoui.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("user")
public class UserController {

    private final SoundRepository soundRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Injection du PasswordEncoder


    public UserController(SoundRepository soundRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.soundRepository = soundRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(){
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/sounds")
    public ResponseEntity<List<Sound>> getUserSounds(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        List<Sound> sounds = soundRepository.findByUserId(userId);
        return ResponseEntity.ok(sounds);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        // Hachage du mot de passe avant de le sauvegarder en base
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }


}
