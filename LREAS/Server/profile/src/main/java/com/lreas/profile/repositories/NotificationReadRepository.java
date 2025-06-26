package com.lreas.profile.repositories;

import com.lreas.profile.models.Notification;
import com.lreas.profile.models.NotificationRead;
import com.lreas.profile.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationReadRepository extends JpaRepository<NotificationRead, String> {
    NotificationRead findByNotificationAndReader(Notification notification, User reader);
}
