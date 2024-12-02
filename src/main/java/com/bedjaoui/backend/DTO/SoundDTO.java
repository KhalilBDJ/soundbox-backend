package com.bedjaoui.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SoundDTO {

    private Long id;
    private String name;
    private int duration;
    private byte[] data;

    public SoundDTO(Long id, String name, int duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
    }
}
