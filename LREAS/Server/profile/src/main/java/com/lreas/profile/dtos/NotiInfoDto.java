package com.lreas.profile.dtos;

import com.lreas.profile.models.Notification;
import com.lreas.profile.models.NotificationRead;

import java.util.Date;

public class NotiInfoDto {
    public String senderName;
    public String receiverName;
    public String message;
    public Date createdTime;
    public Boolean isRead;
    public Date readTime;

    public NotiInfoDto() {}

    public NotiInfoDto(
            Notification notification,
            NotificationRead notificationRead
    ) {
        if (notification == null) {
            return;
        }

        this.senderName = notification.getSender().getUsername();
        this.receiverName = notification.getReceiver().getUsername();
        this.message = notification.getMessage();
        this.createdTime = notification.getCreatedTime();

        if (notificationRead == null) {
            this.isRead = false;
            this.readTime = null;
        }
        else {
            this.isRead = true;
            this.readTime = notificationRead.getReadTime();
        }
    }
}
