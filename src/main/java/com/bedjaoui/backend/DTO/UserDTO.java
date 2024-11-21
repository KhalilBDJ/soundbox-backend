package com.bedjaoui.backend.DTO;

import com.bedjaoui.backend.Model.Sound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private List<Sound> sounds;
}
