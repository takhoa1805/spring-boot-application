package com.lreas.profile.services;

import com.lreas.profile.dtos.NotiInfoDto;
import com.lreas.profile.dtos.UserInfoDto;
import com.lreas.profile.dtos.UserInfoResponse;
import com.lreas.profile.dtos.UserInfoUpdate;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProfileService {
    UserInfoDto getUserInfo(String userId);
    UserInfoResponse getUserInfoFromEmail(String email, String userId);
    UserInfoDto updateUserInfo(MultipartFile file, UserInfoUpdate userInfoUpdate) throws Exception;
    List<NotiInfoDto> getNotification(String userId);
    NotiInfoDto markRead(String notificationId, String userId);
    NotiInfoDto markUnread(String notificationId, String userId);
    NotiInfoDto deleteNotification(String notificationId, String userId);
}
