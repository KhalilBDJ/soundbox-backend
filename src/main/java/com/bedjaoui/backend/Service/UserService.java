package com.bedjaoui.backend.Service;

import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Model.User;
import com.bedjaoui.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public List<Sound> getAllSounds(Long userId) {
        return soundService.getSoundsByUserId(userId);
    }

    public boolean checkIfUserExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public boolean checkIfUserExists(Long userId) {
        return userRepository.findById(userId).isPresent();
    }

    public boolean checkIfUserExists(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password) != null;
    }
}

