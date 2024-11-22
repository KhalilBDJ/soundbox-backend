package com.bedjaoui.backend.Controller;


import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Service.SoundService;
import com.bedjaoui.backend.Service.UserService;
import com.bedjaoui.backend.Util.AuthUtils;
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
    private final AuthUtils authUtils;

    @Autowired
    public SoundController(SoundService soundService, UserService userService, AuthUtils authUtils) {
        this.soundService = soundService;
        this.userService = userService;
        this.authUtils = authUtils;
    }

    // Ajouter un nouveau son pour un utilisateur
    @PostMapping("/user/")
    public ResponseEntity<Sound> addSoundToUser(@RequestBody Sound sound) {
        try{
            Long userId = authUtils.getAuthenticatedUserId();
            if (!userService.checkIfUserExists(userId)) {
                return ResponseEntity.notFound().build(); // Utilisateur non trouvé
            }
            Sound savedSound = soundService.addSoundToUser(userId, sound);
            return ResponseEntity.ok(savedSound);
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(401).build();
        }


    }

    // Récupérer un son par ID
    @GetMapping("/{soundId}")
    public ResponseEntity<Sound> getSoundById(@PathVariable Long soundId) {
        Optional<Sound> sound = soundService.getSoundById(soundId);
        return sound.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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

    @GetMapping("/user/me")
    public ResponseEntity<List<Sound>> getAuthenticatedUserSounds() {
        try {
            Long userId = authUtils.getAuthenticatedUserId();
            List<Sound> sounds = soundService.getSoundsByUserId(userId).orElseThrow(
                    () -> new IllegalArgumentException("No sounds found for this user.")
            );
            return ResponseEntity.ok(sounds);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).build();
        }
    }
}