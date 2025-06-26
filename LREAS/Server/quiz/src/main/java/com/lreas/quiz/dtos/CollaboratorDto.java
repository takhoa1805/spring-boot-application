package com.lreas.quiz.dtos;

import java.util.Date;
import java.util.Objects;

public class CollaboratorDto {
    public transient String userId;
    public transient String sessionId;
    public String avtPath;
    public String username;
    public Date lastActive;

    // object for synchronization
    public transient final Object mutex = new Object();

    public CollaboratorDto() {}

    public CollaboratorDto(
            String userId,
            String sessionId,
            UserInfoDto userInfoDto
    ) {
        if (userInfoDto == null) {
            return;
        }

        this.userId = userId;
        this.sessionId = sessionId;
        this.avtPath = userInfoDto.avtPath;
        this.username = userInfoDto.username;
        this.lastActive = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CollaboratorDto other) {
            return Objects.equals(this.userId, other.userId);
        }
        return false;
    }
}
