package com.example.demo.dataobject.database2;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="users")
public class User2DO {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "username", nullable = false, unique = true)
    @Setter
    private String username;

    @Column(name = "password", nullable = false)
    @Setter
    private String password;
}
