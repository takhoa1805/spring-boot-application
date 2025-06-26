package com.lreas.generator.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name", unique = true, nullable = false)
    @Setter
    private String name;

    @Column(name = "value")
    @Setter
    private String value;
}
