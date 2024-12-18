package com.bedjaoui.backend.Controller;

import com.bedjaoui.backend.DTO.Login.LoginRequestDTO;
import com.bedjaoui.backend.DTO.Login.LoginResponseDTO;
import com.bedjaoui.backend.DTO.RegisterRequestDTO;
import com.bedjaoui.backend.Model.User.User;
import com.bedjaoui.backend.Service.EmailService;
import com.bedjaoui.backend.Service.UserService;
import com.bedjaoui.backend.Util.JwtUtils;
import com.bedjaoui.backend.Util.OtpUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // Ajout du service d'e-mail


    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();


    public AuthController(JwtUtils jwtUtils, UserService userService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        if (userService.checkIfUserExists(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email déjà utilisé."));
        }

        User newUser = userService.addUser(new User(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getUsername(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getPhoneNumber()
        ));

        return ResponseEntity.ok(Map.of(
                "message", "Utilisateur inscrit avec succès.",
                "userId", newUser.getId()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Retourne uniquement les informations de base sans token
            return ResponseEntity.ok(Map.of(
                    "userId", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail()
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants incorrects.");
    }


    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userService.getUserByEmail(email);

        if (user != null) {
            String otp = OtpUtil.generateOtp();
            otpStorage.put(email, otp);

            // Envoi de l'OTP par e-mail
            emailService.sendOtpEmail(email, otp);

            // Retourne un objet JSON au lieu d'un texte brut
            return ResponseEntity.ok(Map.of("message", "OTP envoyé par e-mail."));
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Utilisateur introuvable."));
    }




    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        String storedOtp = otpStorage.get(email);

        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email); // Supprime l'OTP après validation
            User user = userService.getUserByEmail(email);

            // Génération du token JWT après succès
            String jwtToken = jwtUtils.generateToken(
                    user.getEmail(), user.getId(), user.getRole().name(),
                    user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getUsername()
            );

            return ResponseEntity.ok(new LoginResponseDTO(user.getId(), user.getUsername(), jwtToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP invalide ou expiré.");
    }


}