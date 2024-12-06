package com.bedjaoui.backend.Model;

import com.bedjaoui.backend.Model.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private int duration;

}