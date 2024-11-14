package com.bedjaoui.backend.Repository;

import com.bedjaoui.backend.Model.Sound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SoundRepository extends JpaRepository<Sound, Long> {
    Optional<List<Sound>> findByUserId(Long userId);

}
