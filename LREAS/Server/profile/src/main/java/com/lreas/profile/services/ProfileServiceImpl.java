package com.lreas.profile.services;

import com.lreas.profile.dtos.NotiInfoDto;
import com.lreas.profile.dtos.UserInfoDto;
import com.lreas.profile.dtos.UserInfoResponse;
import com.lreas.profile.dtos.UserInfoUpdate;
import com.lreas.profile.models.Notification;
import com.lreas.profile.models.NotificationRead;
import com.lreas.profile.models.User;
import com.lreas.profile.repositories.NotificationReadRepository;
import com.lreas.profile.repositories.NotificationRepository;
import com.lreas.profile.repositories.UserRepository;
import com.lreas.profile.utils.MinioClientUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository notificationReadRepository;
    private final MinioClientUtils minioClientUtils;

    public ProfileServiceImpl(
            UserRepository userRepository,
            NotificationRepository notificationRepository,
            NotificationReadRepository notificationReadRepository,
            MinioClientUtils minioClientUtils
    ) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.notificationReadRepository = notificationReadRepository;
        this.minioClientUtils = minioClientUtils;
    }

    public UserInfoDto getUserInfo(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );
        return new UserInfoDto(user, this.minioClientUtils);
    }

    public UserInfoResponse getUserInfoFromEmail(String email, String userId) {
        User user = userRepository.findByEmail(email);
        User currUser = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        if (user == null || !currUser.getInstitution().getId().equals(user.getInstitution().getId())) {
            throw new RuntimeException("User Not Found");
        }

        return new UserInfoResponse(user);
    }

    public UserInfoDto updateUserInfo(
            MultipartFile file,
            UserInfoUpdate userInfoUpdate
    ) throws Exception {
        User user = userRepository.findById(userInfoUpdate.userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        if (file != null && !file.isEmpty()) {
            // upload avatar for user profile
            String objectName = user.getId();

            // setup metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("name", file.getName());
            metadata.put("size", Long.toString(file.getSize()));
            metadata.put("type", file.getContentType());
            metadata.put("filename", file.getOriginalFilename());
            metadata.put("contentType", file.getContentType());
            metadata.put("last_modified", String.valueOf((new Date().getTime() / 1000))); // unix timestamp
            metadata.put("status", MinioClientUtils.FILE_STATUS.AVAILABLE.toString());

            this.minioClientUtils.uploadFile(
                    objectName,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType(),
                    metadata
            );

            // verify avatar upload
            this.minioClientUtils.assertExistence(objectName);

            // update avatar path for user
            user.setAvtPath(objectName);
        }

        user.setUsername(userInfoUpdate.username);
        user.setBirthday(userInfoUpdate.birthday);
        user.setGender(userInfoUpdate.gender);

        if (userInfoUpdate.gender.compareTo(User.GENDER.OTHER) == 0) {
            user.setOtherGender(userInfoUpdate.otherGender);
        }

        user.setEmail(userInfoUpdate.email);
        user.setDescription(userInfoUpdate.description);
        user.setPhoneNumber(userInfoUpdate.phone);
        user.setAddress(userInfoUpdate.address);
        userRepository.save(user);

        return new UserInfoDto(user, this.minioClientUtils);
    }

    private Object[] checkNotification(
            String notificationId,
            String userId
    ) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new RuntimeException("Notification Not Found")
        );

        NotificationRead notificationRead = notificationReadRepository.findByNotificationAndReader(
                notification, user
        );

        return new Object[]{user, notification, notificationRead};
    }

    public List<NotiInfoDto> getNotification(
            String userId
    ) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        List<Notification> notifications = notificationRepository.findByReceiver(user);
        notifications.addAll(notificationRepository.findBySenderAndReceiver(null, null));

        List<NotiInfoDto> notiInfoDtos = new LinkedList<>();
        for (Notification notification : notifications) {
            NotificationRead notificationRead = notificationReadRepository.findByNotificationAndReader(notification, user);
            notiInfoDtos.add(new NotiInfoDto(notification, notificationRead));
        }

        return notiInfoDtos;
    }

    public NotiInfoDto markRead(
            String notificationId,
            String userId
    ) {
        // check
        Object[] objects = this.checkNotification(
                notificationId, userId
        );
        User user = (User) objects[0];
        Notification notification = (Notification) objects[1];

        NotificationRead notificationRead = notificationReadRepository.findByNotificationAndReader(
                notification, user
        );
        if (notificationRead == null) {
            notificationRead = new NotificationRead();
            notificationRead.setNotification(notification);
            notificationRead.setReader(user);
            notificationRead.setReadTime(new Date());
            notificationReadRepository.save(notificationRead);
        }

        return new NotiInfoDto(notification, notificationRead);
    }

    public NotiInfoDto markUnread(
            String notificationId,
            String userId
    ) {
        // check
        Object[] objects = this.checkNotification(
                notificationId, userId
        );
        Notification notification = (Notification) objects[1];
        NotificationRead notificationRead = (NotificationRead) objects[2];

        if (notificationRead != null) {
            notificationReadRepository.delete(notificationRead);
        }

        return new NotiInfoDto(notification, notificationRead);
    }

    public NotiInfoDto deleteNotification(
            String notificationId,
            String userId
    ) {
        // check
        Object[] objects = this.checkNotification(
                notificationId, userId
        );
        Notification notification = (Notification) objects[1];
        NotificationRead notificationRead = (NotificationRead) objects[2];

        if (notificationRead != null) {
            notificationReadRepository.delete(notificationRead);
        }

        NotiInfoDto notiInfoDto = new NotiInfoDto(notification, notificationRead);

        notificationRepository.delete(notification);

        return notiInfoDto;
    }
}
