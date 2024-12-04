package com.bedjaoui.backend.Service;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class YouTubeService {

    private final RestTemplate restTemplate;

    public YouTubeService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> downloadFromYouTube(String youtubeUrl) {
        String pythonServiceUrl = "http://localhost:5000/convert"; // URL du service Python
        Map<String, String> request = new HashMap<>();
        request.put("url", youtubeUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    pythonServiceUrl,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode responseBody = response.getBody();
                Map<String, Object> result = new HashMap<>();
                result.put("name", responseBody.get("name").asText());
                result.put("duration", responseBody.get("duration").asInt());
                result.put("audioBase64", responseBody.get("audio_base64").asText());
                return result;
            } else {
                throw new RuntimeException("Failed to download from YouTube: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling Python service: " + e.getMessage(), e);
        }
    }
}