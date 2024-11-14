package com.bedjaoui.backend.Service;

import com.bedjaoui.backend.Model.Sound;
import com.bedjaoui.backend.Repository.SoundRepository;
import com.bedjaoui.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoundService {

    private final SoundRepository soundRepository;

    @Autowired
    public SoundService(SoundRepository soundRepository) {
        this.soundRepository = soundRepository;
    }

    public List<Sound> getSoundsByUserId(Long userId) {
        return soundRepository.findByUserId(userId);
    }

    public Sound addSound(Sound sound) {
        return soundRepository.save(sound);
    }

    public Sound updateSound(Sound sound) {
        return soundRepository.save(sound);
    }
}
