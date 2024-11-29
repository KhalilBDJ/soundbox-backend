package com.bedjaoui.backend.Security.config;

import com.bedjaoui.backend.Filter.JwtAuthenticationFilter;
import com.bedjaoui.backend.Filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, CorsFilter corsFilter) throws Exception {
        http
                // Désactiver CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Gérer les requêtes autorisées
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Autoriser les endpoints publics
                        .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN uniquement
                        .anyRequest().hasRole("USER") // USER par défaut pour tout le reste
                )

                // Ajout des filtres dans le bon ordre
                .addFilterBefore(corsFilter, JwtAuthenticationFilter.class) // Le filtre CORS avant le filtre JWT
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Filtre JWT

                // Gestion des sessions stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
