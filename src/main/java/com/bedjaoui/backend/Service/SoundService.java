package com.bedjaoui.backend.Service;

import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Model.User.User;
import com.bedjaoui.backend.DTO.SoundDTO;
import com.bedjaoui.backend.Repository.SoundRepository;
import com.bedjaoui.backend.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


//TODO: check pourquoi la data est null
@Service
public class SoundService {

    private final SoundRepository soundRepository;
    private final UserRepository userRepository;

    @Autowired
    public SoundService(SoundRepository soundRepository, UserRepository userRepository) {
        this.soundRepository = soundRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addSoundToUser(Long userId, MultipartFile file, String name, int duration) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        try {
            Sound sound = new Sound();
            sound.setUser(user);
            sound.setData(file.getBytes());
            sound.setName(name);
            sound.setDuration(duration);

            soundRepository.save(sound);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to process the file", e);
        }
    }


    @Transactional
    public List<SoundDTO> getSoundsByUserId(Long userId) {
        List<Sound> sounds = soundRepository.findByUserId(userId) .orElseThrow(() -> new RuntimeException("User not found for id: " + userId));
        return sounds.stream().map(this::toSoundDTO).toList();
    }

    @Transactional
    public SoundDTO getSoundById(Long soundId) {
        Sound sound = soundRepository.findById(soundId) .orElseThrow(() -> new RuntimeException("Sound not found for id: " + soundId));
        return toSoundDTO(sound);
    }

    @Transactional
    public byte[] getSoundData(Long soundId) {
        Sound sound = soundRepository.findById(soundId)
                .orElseThrow(() -> new RuntimeException("Sound not found for id: " + soundId));
        if (sound.getData() == null || sound.getData().length == 0) {
            throw new IllegalStateException("No data available for sound with id: " + soundId);
        }
        return sound.getData();
    }


    public boolean checkIfSoundExists(Long soundId) {
        return soundRepository.findById(soundId).isPresent();
    }

    public void deleteSoundById(Long soundId) {
        soundRepository.deleteById(soundId);
    }


    public SoundDTO toSoundDTO(Sound sound) {
        SoundDTO dto = new SoundDTO();
        dto.setId(sound.getId());
        dto.setName(sound.getName());
        dto.setDuration(sound.getDuration());
        dto.setData(sound.getData()); // Inclure les données
        return dto;
    }

}