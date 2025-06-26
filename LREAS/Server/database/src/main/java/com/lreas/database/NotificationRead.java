package com.lreas.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
public class NotificationRead {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private User reader;

    @Column(name = "read_time")
    @Setter
    private Date readTime;
}
