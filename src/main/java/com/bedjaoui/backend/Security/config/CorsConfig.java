package com.bedjaoui.backend.Security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200") // Origine Angular
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes autorisées
                        .allowedHeaders("Authorization", "Content-Type", "X-Requested-With") // En-têtes autorisés
                        .exposedHeaders("Authorization") // En-têtes exposés dans la réponse
                        .allowCredentials(true); // Autoriser les cookies et sessions si nécessaire
            }
        };
    }
}
