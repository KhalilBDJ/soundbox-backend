package com.bedjaoui.backend.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
//TODO : Faire un DTO pour ne recevoir que le JWT Token
@Entity
@Getter
@Setter
@Table(name = "app_user")  // Change le nom de la table pour éviter le conflit avec le mot réservé "user"
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sound> sounds;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {

    }
}
