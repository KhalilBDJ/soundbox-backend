package com.bedjaoui.backend.Controller;


import com.bedjaoui.backend.DTO.SoundDTO;
import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Service.SoundService;
import com.bedjaoui.backend.Service.UserService;
import com.bedjaoui.backend.Util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;

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
    public ResponseEntity<String> uploadSoundToUser(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("name") String name,
                                                      @RequestParam("duration") int duration) {
        try {
            Long userId = authUtils.getAuthenticatedUserId();
            if (!userService.checkIfUserExists(userId)) {
                return ResponseEntity.notFound().build(); // Utilisateur non trouvé
            }
            soundService.addSoundToUser(userId, file, name, duration);
            return ResponseEntity.ok("Sound added successfully + " + name);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null); // Utilisateur non trouvé
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500).body(null); // Erreur de traitement du fichier
        }
    }

    // Récupérer un son par ID
    @GetMapping("/{soundId}")
    public ResponseEntity<SoundDTO> getSoundById(@PathVariable Long soundId) {
        SoundDTO soundDTO = soundService.getSoundById(soundId);
        return ResponseEntity.ok(soundDTO);
    }

    @DeleteMapping("/{soundId}")
    public ResponseEntity<Void> deleteSoundById(@PathVariable Long soundId) {
        if (!soundService.checkIfSoundExists(soundId)) {
            return ResponseEntity.notFound().build();
        }
        soundService.deleteSoundById(soundId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{soundId}/data")
    public ResponseEntity<Resource> getSoundData(@PathVariable Long soundId) {
        try {
            // Récupérer les données du son
            byte[] soundData = soundService.getSoundData(soundId);

            // Créer une ressource pour le téléchargement
            ByteArrayResource resource = new ByteArrayResource(soundData);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"sound_" + soundId + ".mp3\"")
                    .contentLength(soundData.length)
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Son non trouvé
        }
    }


    @GetMapping("/user/me")
    public ResponseEntity<List<SoundDTO>> getAuthenticatedUserSounds() {
        try {
            Long userId = authUtils.getAuthenticatedUserId();
            List<SoundDTO> sounds = soundService.getSoundsByUserId(userId);
            return ResponseEntity.ok(sounds);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).build();
        }
    }
}