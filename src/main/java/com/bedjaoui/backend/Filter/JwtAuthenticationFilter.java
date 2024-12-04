package com.bedjaoui.backend.Filter;

import com.bedjaoui.backend.Security.JwtAuthenticationToken;
import com.bedjaoui.backend.Util.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;

@Component
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

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);

            try {
                var claims = jwtUtils.parseToken(jwtToken);
                String username = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                String role = claims.get("role", String.class);

                JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                        username, userId, role, List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
