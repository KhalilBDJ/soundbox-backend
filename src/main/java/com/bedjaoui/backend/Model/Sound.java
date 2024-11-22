package com.bedjaoui.backend.Model;

import com.bedjaoui.backend.Model.User.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
//TODO : Mettre un DTO pour retirer l'utilisateur en retour d'appel de l'API
@Entity
@Getter
@Setter
public class Sound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private byte[] data;

    @Column(nullable = false)
    private String name;

    private int duration; // Duration in seconds, for example
}