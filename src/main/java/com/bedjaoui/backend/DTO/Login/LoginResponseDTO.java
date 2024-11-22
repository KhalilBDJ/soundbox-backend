package com.bedjaoui.backend.DTO.Login;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private Long userId;
    private String email;
    private String jwtToken;

}
