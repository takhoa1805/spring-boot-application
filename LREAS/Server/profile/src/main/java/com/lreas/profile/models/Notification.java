package com.lreas.profile.models;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private Institution institution;

    @Column(name = "created_time")
    @Setter
    private Date createdTime;

    @Column(name = "message")
    @Setter
    private String message;
}
