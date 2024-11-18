package com.bedjaoui.backend.DTO;

import com.bedjaoui.backend.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String password;  // Ajout du mot de passe pour l'inscription et le login

}
