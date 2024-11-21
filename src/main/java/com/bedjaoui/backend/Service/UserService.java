package com.bedjaoui.backend.Service;

import com.bedjaoui.backend.DTO.UserDTO;
import com.bedjaoui.backend.Model.User.Role;
import com.bedjaoui.backend.Model.User.User;
import com.bedjaoui.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Ajouter un nouvel utilisateur avec un mot de passe haché
    public User addUser(User user) {
        user.setEmail(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    // Récupérer tous les utilisateurs
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail(), null))
                .collect(Collectors.toList());
    }

    // Récupérer un utilisateur par ID
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(user -> new UserDTO(user.getId(), user.getEmail(), null));
    }

    // Récupérer un utilisateur par email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Vérifier si un utilisateur existe par email
    public boolean checkIfUserExists(String email) {
        return userRepository.findByEmail(email)!=
                null;
    }

    // Vérifier si un utilisateur existe par ID
    public boolean checkIfUserExists(Long userId) {
        return userRepository.findById(userId).isPresent();
    }
}
