package com.bedjaoui.backend.Security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final Long userId;
    private final String role;


    public JwtAuthenticationToken(String username, Long userId, String role, Collection<? extends GrantedAuthority> authorities) {
        super(authorities); // Définit les autorités associées
        this.username = username;
        this.userId = userId;
        this.role = role;
        setAuthenticated(true); // Marque comme authentifié
    }

    @Override
    public Object getCredentials() {
        return null; // Aucun mot de passe ou autre donnée sensible n'est nécessaire ici
    }

    @Override
    public Object getPrincipal() {
        return username; // Le nom d'utilisateur est considéré comme le principal
    }

}
