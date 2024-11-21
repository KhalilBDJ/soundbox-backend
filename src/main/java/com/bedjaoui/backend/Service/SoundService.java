package com.bedjaoui.backend.Service;

import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Model.User.User;
import com.bedjaoui.backend.Repository.SoundRepository;
import com.bedjaoui.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SoundService {

    private final SoundRepository soundRepository;
    private final UserRepository userRepository;

    @Autowired
    public SoundService(SoundRepository soundRepository, UserRepository userRepository) {
        this.soundRepository = soundRepository;
        this.userRepository = userRepository;
    }

    // Ajouter un nouveau son pour un utilisateur
    public Sound addSoundToUser(Long userId, Sound sound) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        sound.setUser(user);
        return soundRepository.save(sound);
    }

    // Récupérer tous les sons d'un utilisateur
    public Optional<List<Sound>> getSoundsByUserId(Long userId) {
        return soundRepository.findByUserId(userId);
    }

    // Récupérer un son par ID
    public Optional<Sound> getSoundById(Long soundId) {
        return soundRepository.findById(soundId);
    }

    // Vérifier si un son existe par ID
    public boolean checkIfSoundExists(Long soundId) {
        return soundRepository.findById(soundId).isPresent();
    }

    // Supprimer un son par ID
    public void deleteSoundById(Long soundId) {
        soundRepository.deleteById(soundId);
    }
}