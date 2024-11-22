package com.bedjaoui.backend.Controller;

import com.bedjaoui.backend.DTO.Login.LoginRequestDTO;
import com.bedjaoui.backend.DTO.Login.LoginResponseDTO;
import com.bedjaoui.backend.DTO.RegisterRequestDTO;
import com.bedjaoui.backend.Model.User.User;
import com.bedjaoui.backend.Service.UserService;
import com.bedjaoui.backend.Util.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtils jwtUtils, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            String jwtToken = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole().name());
            return ResponseEntity.ok(new LoginResponseDTO(user.getId(), user.getEmail(), jwtToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}