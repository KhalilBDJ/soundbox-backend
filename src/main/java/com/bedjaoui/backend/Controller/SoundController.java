package com.bedjaoui.backend.Controller;

import com.bedjaoui.backend.DTO.SoundDTO;
import com.bedjaoui.backend.Service.SoundService;
import com.bedjaoui.backend.Service.UserService;
import com.bedjaoui.backend.Util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
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

    @Autowired
    public SoundController(SoundService soundService, UserService userService, AuthUtils authUtils) {
        this.soundService = soundService;
        this.userService = userService;
        this.authUtils = authUtils;
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

    @PostMapping("/user/bytes")
    public ResponseEntity<Map<String, String>> uploadSoundToUser(
            @RequestBody Map<String, Object> requestData) {
        try {
            String base64Audio = (String) requestData.get("data");
            String name = (String) requestData.get("name");
            int duration = ((Number) requestData.get("duration")).intValue();

            byte[] audioBytes = java.util.Base64.getDecoder().decode(base64Audio);

            Long userId = authUtils.getAuthenticatedUserId();

            soundService.addSoundToUser(userId, audioBytes, name, duration);

            return ResponseEntity.ok(Map.of("message", "Sound added successfully", "name", name));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error processing the sound: " + e.getMessage()));
        }
    }

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
            return ResponseEntity.notFound().build();
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

            // Appel au service Python pour récupérer l'audio depuis YouTube
            RestTemplate restTemplate = new RestTemplate();
            String pythonUrl = "http://localhost:5000/convert"; // Endpoint Python pour YouTube
            return getMapResponseEntity(youtubeUrl, name, userId, restTemplate, pythonUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error processing YouTube sound."));
        }
    }

    @PutMapping("/user/{soundId}")
    public ResponseEntity<Map<String, String>> updateSoundName(@PathVariable Long soundId, @RequestBody Map<String, String> updates) {
        try {
            if (!soundService.checkIfSoundExists(soundId)) {
                return ResponseEntity.notFound().build();
            }

            Long userId = authUtils.getAuthenticatedUserId();

            if (!soundService.isUserOwnerOfSound(userId, soundId)) {
                return ResponseEntity.status(403).body(Map.of("error", "You do not have permission to update this sound."));
            }

            String newName = updates.get("name");
            if (newName == null || newName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid name."));
            }

            soundService.updateSoundName(soundId, newName);

            return ResponseEntity.ok(Map.of("message", "Sound name updated successfully.", "name", newName));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("error", "An error occurred while updating the sound name."));
        }
    }

    @PostMapping("/user/youtube/preview")
    public ResponseEntity<Map<String, Object>> getPreviewFromYouTube(@RequestParam("url") String youtubeUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String pythonUrl = "http://localhost:5000/convert"; // On réutilise l'endpoint /convert
            return getMapResponseEntity(youtubeUrl, restTemplate, pythonUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/trim")
    public ResponseEntity<Map<String, Object>> trimAudio(@RequestBody Map<String, Object> requestData) {
        try {
            String audioBase64 = (String) requestData.get("audioBase64");
            double start = ((Number) requestData.get("start")).doubleValue();
            double end = ((Number) requestData.get("end")).doubleValue();

            RestTemplate restTemplate = new RestTemplate();
            String pythonUrl = "http://localhost:5000/trim";
            Map<String, Object> pythonRequest = Map.of(
                    "audio_base64", audioBase64,
                    "start", start,
                    "end", end
            );

            Map<String, Object> pythonResponse = restTemplate.postForObject(pythonUrl, pythonRequest, Map.class);

            return ResponseEntity.ok(pythonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error communicating with Python script: " + e.getMessage()));
        }
    }


    private ResponseEntity<Map<String, String>> getMapResponseEntity(@RequestParam("url") String tiktokUrl, @RequestParam(value = "name", required = false) String name, Long userId, RestTemplate restTemplate, String pythonUrl) {
        Map<String, String> pythonRequest = Map.of("url", tiktokUrl);

        Map<String, Object> soundData = restTemplate.postForObject(pythonUrl, pythonRequest, Map.class);

        String base64Audio = (String) soundData.get("audio_base64");
        byte[] audioData = Base64.getDecoder().decode(base64Audio);

        String finalName = (name != null && !name.trim().isEmpty()) ? name : (String) soundData.get("name");
        int duration = (int) soundData.get("duration");

        soundService.addSoundToUser(userId, audioData, finalName, duration);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Sound added successfully");
        response.put("name", finalName);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/user/instagram/preview")
    public ResponseEntity<Map<String, Object>> getPreviewFromInstagram(@RequestParam("url") String instagramUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String pythonUrl = "http://localhost:5000/convert/instagram";

            return getMapResponseEntity(instagramUrl, restTemplate, pythonUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user/tiktok/preview")
    public ResponseEntity<Map<String, Object>> getPreviewFromTikTok(@RequestParam("url") String tiktokUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String pythonUrl = "http://localhost:5000/convert/tiktok";

            return getMapResponseEntity(tiktokUrl, restTemplate, pythonUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private ResponseEntity<Map<String, Object>> getMapResponseEntity(@RequestParam("url") String tiktokUrl, RestTemplate restTemplate, String pythonUrl) {
        Map<String, String> pythonRequest = Map.of("url", tiktokUrl);
        Map<String, Object> soundData = restTemplate.postForObject(pythonUrl, pythonRequest, Map.class);

        String base64Audio = (String) soundData.get("audio_base64");
        String name = (String) soundData.get("name");
        int duration = (int) soundData.get("duration");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("name", name);
        responseBody.put("duration", duration);
        responseBody.put("audioData", base64Audio);

        return ResponseEntity.ok(responseBody);
    }

}
