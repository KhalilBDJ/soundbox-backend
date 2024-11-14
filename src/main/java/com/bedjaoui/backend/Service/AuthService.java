package com.bedjaoui.backend.Service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bedjaoui.backend.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;
@Service
public class AuthService implements UserDetailsService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Algorithm algorithm;

    @Autowired
    public AuthService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        String SECRET_KEY = "secret";
        this.algorithm = Algorithm.HMAC256(SECRET_KEY); // Algorithme de signature avec clé secrète
    }

    // Méthode pour charger les informations utilisateur pour Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    // Méthode d'authentification utilisateur avec génération de token JWT
    public String authenticate(String email, String password) {
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return generateToken(user);
        } else {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    // Génère un token JWT avec java-jwt d'Auth0
    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000)) // Expire dans 24h
                .sign(algorithm); // Signer le token avec l'algorithme HMAC256 et la clé secrète
    }

    // Valide le token JWT
    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build(); // Créer le vérificateur de token
            verifier.verify(token); // Valide le token
            return true;
        } catch (JWTVerificationException e) {
            return false; // Le token est invalide
        }
    }

    // Extrait le nom d'utilisateur (email) du token JWT
    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token); // Décoder le token sans le vérifier
        return decodedJWT.getSubject(); // Extraire le "subject" (email) du token
    }
}