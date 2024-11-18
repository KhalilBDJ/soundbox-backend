package com.bedjaoui.backend.Controller;

import com.bedjaoui.backend.DTO.LoginRequestDTO;
import com.bedjaoui.backend.DTO.LoginResponseDTO;
import com.bedjaoui.backend.DTO.RegisterRequestDTO;
import com.bedjaoui.backend.Model.User;
import com.bedjaoui.backend.Service.AuthService;
import com.bedjaoui.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // Endpoint pour l'inscription d'un utilisateur
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequest) {
        if (userService.checkIfUserExists(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email déjà utilisé.");
        }
        User newUser = userService.addUser(new User(registerRequest.getEmail(), registerRequest.getPassword()));
        return ResponseEntity.ok("Utilisateur inscrit avec succès avec l'ID: " + newUser.getId());
    }

    // Endpoint pour l'authentification (login)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        Optional<User> authenticatedUser = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();
            String jwtToken = authService.generateJwtToken(user);
            LoginResponseDTO responseDTO = new LoginResponseDTO(user.getId(), user.getEmail(), jwtToken);
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }
}