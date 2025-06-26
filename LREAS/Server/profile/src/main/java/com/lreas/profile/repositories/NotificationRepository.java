package com.lreas.profile.repositories;

import com.lreas.profile.models.Notification;
import com.lreas.profile.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findBySenderAndReceiver(User sender, User receiver);
    List<Notification> findByReceiver(User receiver);
}
