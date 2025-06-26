package com.example.demo.dataobject.database1;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="users")
public class UserDO {
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
