package com.lreas.generator.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "type")
    @Setter
    private String type;

    @Column(name = "file_path", unique = true)
    @Setter
    private String file_path;

    @OneToOne
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    @Setter
    private Resource resource;

}
