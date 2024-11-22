package com.bedjaoui.backend.Util;

import com.bedjaoui.backend.Security.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
            return jwtAuthToken.getUserId();
        }

        throw new IllegalStateException("Authentication token is invalid");
    }
}
