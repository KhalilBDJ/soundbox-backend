package com.bedjaoui.backend.Service;

import com.bedjaoui.backend.DTO.UserDTO;
import com.bedjaoui.backend.Model.User.Role;
import com.bedjaoui.backend.Model.User.User;
import com.bedjaoui.backend.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SoundService soundService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SoundService soundService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.soundService = soundService;
    }

    public User addUser(User user) {
        user.setEmail(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setPhoneNumber(user.getPhoneNumber());
        user.setUsername(user.getUsername());
        return userRepository.save(user);
    }

    @Transactional
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail(), soundService.getSoundsByUserId(user.getId())))
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkIfUserExists(String email) {
        return userRepository.findByEmail(email)!=
                null;
    }

    public boolean checkIfUserExists(Long userId) {
        return userRepository.findById(userId).isEmpty();
    }
}
