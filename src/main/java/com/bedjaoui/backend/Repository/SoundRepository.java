package com.bedjaoui.backend.Repository;

import com.bedjaoui.backend.Model.Sound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoundRepository extends JpaRepository<Sound, Long> {
    List<Sound> findByUserId(Long userId);

}
