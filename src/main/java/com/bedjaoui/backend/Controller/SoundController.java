package com.bedjaoui.backend.Controller;


import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Service.SoundService;
import com.bedjaoui.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sounds")
public class SoundController {

    private final SoundService soundService;
    private final UserService userService;

    @Autowired
    public SoundController(SoundService soundService, UserService userService) {
        this.soundService = soundService;
        this.userService = userService;
    }

    // Ajouter un nouveau son pour un utilisateur
    @PostMapping("/user/{userId}")
    public ResponseEntity<Sound> addSoundToUser(@PathVariable Long userId, @RequestBody Sound sound) {
        if (!userService.checkIfUserExists(userId)) {
            return ResponseEntity.notFound().build(); // Utilisateur non trouvé
        }
        Sound savedSound = soundService.addSoundToUser(userId, sound);
        return ResponseEntity.ok(savedSound);
    }

    // Récupérer tous les sons d'un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Sound>> getUserSounds(@PathVariable Long userId) {
        if (!userService.checkIfUserExists(userId)) {
            return ResponseEntity.notFound().build(); // Utilisateur non trouvé
        }
        List<Sound> sounds = soundService.getSoundsByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(sounds);
    }

    // Récupérer un son par ID
    @GetMapping("/{soundId}")
    public ResponseEntity<Sound> getSoundById(@PathVariable Long soundId) {
        Optional<Sound> sound = soundService.getSoundById(soundId);
        if (sound.isPresent()) {
            return ResponseEntity.ok(sound.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un son par ID
    @DeleteMapping("/{soundId}")
    public ResponseEntity<Void> deleteSoundById(@PathVariable Long soundId) {
        if (!soundService.checkIfSoundExists(soundId)) {
            return ResponseEntity.notFound().build(); // Son non trouvé
        }
        soundService.deleteSoundById(soundId);
        return ResponseEntity.noContent().build();
    }
}