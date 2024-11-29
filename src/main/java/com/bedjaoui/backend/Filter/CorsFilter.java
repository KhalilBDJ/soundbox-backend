package com.bedjaoui.backend.Filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
@Order(1)
public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {
        // Ajouter les en-têtes CORS
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200"); // Autoriser l'origine Angular
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // Méthodes autorisées
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With"); // En-têtes autorisés
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Autoriser les cookies/sessions

        // Gérer les requêtes OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return; // Ne pas transmettre les requêtes OPTIONS au filtre suivant
        }

        // Transmettre la requête au filtre suivant
        filterChain.doFilter(request, response);
    }

}
