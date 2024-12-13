package com.bedjaoui.backend.Controller;


import com.bedjaoui.backend.DTO.SoundDTO;
import com.bedjaoui.backend.Service.SoundService;
import com.bedjaoui.backend.Service.UserService;
import com.bedjaoui.backend.Service.YouTubeService;
import com.bedjaoui.backend.Util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Base64;


@RestController
@RequestMapping("/sounds")
public class SoundController {

    private final SoundService soundService;
    private final UserService userService;
    private final AuthUtils authUtils;
    private final YouTubeService youTubeService;

    @Autowired
    public SoundController(SoundService soundService, UserService userService, AuthUtils authUtils, YouTubeService youTubeService) {
        this.soundService = soundService;
        this.userService = userService;
        this.authUtils = authUtils;
        this.youTubeService = youTubeService;
    }

    @PostMapping("/user/")
    public ResponseEntity<Map<String, String>> uploadSoundToUser(@RequestParam("data") MultipartFile data,
                                                                 @RequestParam("name") String name,
                                                                 @RequestParam("duration") int duration) {
        try {
            Long userId = authUtils.getAuthenticatedUserId();
            if (userService.checkIfUserExists(userId)) {
                return ResponseEntity.notFound().build();
            }
            soundService.addSoundToUser(userId, data, name, duration);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sound added successfully");
            response.put("name", name);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
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
            byte[] soundData = soundService.getSoundData(soundId);

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

    @PostMapping("/user/youtube")
    public ResponseEntity<Map<String, String>> uploadSoundFromYouTube(@RequestParam("url") String youtubeUrl,
                                                                      @RequestParam(value = "name", required = false) String name) {
        try {
            Long userId = authUtils.getAuthenticatedUserId();
            if (userService.checkIfUserExists(userId)) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> soundData = youTubeService.downloadFromYouTube(youtubeUrl);

            String base64Audio = (String) soundData.get("audioBase64");
            byte[] audioData = Base64.getDecoder().decode(base64Audio);

            // Utiliser le nom fourni ou celui de la vidéo
            String finalName = (name != null && !name.trim().isEmpty()) ? name : (String) soundData.get("name");
            int duration = (int) soundData.get("duration");

            soundService.addSoundToUser(userId, audioData, finalName, duration);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Sound added successfully");
            response.put("name", finalName);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error processing YouTube sound."));
        }
    }


    @PutMapping("/user/{soundId}")
    public ResponseEntity<Map<String, String>> updateSoundName(@PathVariable Long soundId, @RequestBody Map<String, String> updates) {
        try {
            // Vérifier si le son existe
            if (!soundService.checkIfSoundExists(soundId)) {
                return ResponseEntity.notFound().build();
            }

            // Obtenir l'utilisateur authentifié
            Long userId = authUtils.getAuthenticatedUserId();

            // Vérifier si l'utilisateur possède le son
            if (!soundService.isUserOwnerOfSound(userId, soundId)) {
                return ResponseEntity.status(403).body(Map.of("error", "You do not have permission to update this sound."));
            }

            // Récupérer le nouveau nom depuis la requête
            String newName = updates.get("name");
            if (newName == null || newName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid name."));
            }

            // Mise à jour du nom du son
            soundService.updateSoundName(soundId, newName);

            // Retourner une réponse de succès
            return ResponseEntity.ok(Map.of("message", "Sound name updated successfully.", "name", newName));
        } catch (RuntimeException e) {
            // Gestion des erreurs internes
            return ResponseEntity.status(500).body(Map.of("error", "An error occurred while updating the sound name."));
        }
    }

    // Dans SoundController
    @PostMapping("/user/youtube/preview")
    public ResponseEntity<byte[]> getPreviewFromYouTube(@RequestParam("url") String youtubeUrl) {
        try {
            Map<String, Object> soundData = youTubeService.downloadFromYouTube(youtubeUrl);

            String base64Audio = (String) soundData.get("audioBase64");
            byte[] audioData = java.util.Base64.getDecoder().decode(base64Audio);

            String name = (String) soundData.get("name");
            int duration = (int) soundData.get("duration");

            return ResponseEntity.ok()
                    .header("x-audio-name", name)
                    .header("x-audio-duration", String.valueOf(duration))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(audioData);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/trim")
    public ResponseEntity<Map<String, Object>> trimAudio(@RequestBody Map<String, Object> requestData) {
        try {
            // Récupérer les données de la requête
            String audioBase64 = (String) requestData.get("audioBase64");
            double start = ((Number) requestData.get("start")).doubleValue();
            double end = ((Number) requestData.get("end")).doubleValue();

            // Préparer la requête pour le script Python
            RestTemplate restTemplate = new RestTemplate();
            String pythonUrl = "http://localhost:5000/trim"; // URL du script Python
            Map<String, Object> pythonRequest = Map.of(
                    "audio_base64", audioBase64,
                    "start", start,
                    "end", end
            );

            // Appeler le script Python
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(pythonRequest);
            Map<String, Object> pythonResponse = restTemplate.postForObject(pythonUrl, entity, Map.class);

            // Retourner la réponse au frontend
            return ResponseEntity.ok(pythonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error communicating with Python script: " + e.getMessage()));
        }
    }


}