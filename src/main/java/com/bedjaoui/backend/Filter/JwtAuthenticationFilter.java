package com.bedjaoui.backend.Filter;

import com.bedjaoui.backend.Security.JwtAuthenticationToken;
import com.bedjaoui.backend.Util.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;

@Component
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, @NonNull
                                    jakarta.servlet.http.HttpServletResponse response, @NonNull
                                    jakarta.servlet.FilterChain filterChain)
            throws jakarta.servlet.ServletException, IOException {

        // Récupérer le header Authorization
        String authHeader = request.getHeader("Authorization");

        // Vérifier si le header contient un JWT
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7); // Extraire le token sans "Bearer "

            try {
                // Valider le token et extraire les claims
                var claims = jwtUtils.parseToken(jwtToken); // Utilisez votre classe JwtUtils pour valider
                String username = claims.getSubject(); // Extraire le nom d'utilisateur
                Long userId = claims.get("userId", Long.class); // Extraire l'ID utilisateur
                String role = claims.get("role", String.class); // Extraire le rôle

                // Créer une authentification basée sur le token
                JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                        username, userId, role, List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // En cas de token invalide, envoyer une réponse Unauthorized
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        // Transmettre la requête au filtre suivant
        filterChain.doFilter(request, response);
    }
}
