package com.lreas.quiz.dtos;

import com.lreas.quiz.models.User;
import com.lreas.quiz.utils.MinioClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class UserInfoDto {
    private final static Logger logger = LoggerFactory.getLogger(UserInfoDto.class);

    public String avtPath;
    public String username;
    public Date birthday;
    public User.GENDER gender;
    public String otherGender;
    public String description;
    public String email;
    public String phone;
    public String address;
    public String institutionName;
    public String invitationCode;

    public UserInfoDto() {}

    public UserInfoDto(
            User user, MinioClientUtils minioClientUtils
    ) {
        try {
            this.avtPath = minioClientUtils.getUrl(user.getId());
        }
        catch (Exception e) {
            this.avtPath = null;
            logger.error("Cannot get avatar path: ", e);
        }
        this.username = user.getUsername();
        this.birthday = user.getBirthday();
        this.gender = user.getGender();
        this.otherGender = user.getOtherGender();
        this.description = user.getDescription();
        this.email = user.getEmail();
        this.phone = user.getPhoneNumber();
        this.address = user.getAddress();
        this.institutionName = user.getInstitution().getName();
        this.invitationCode = user.getInvitationCode();
    }
}
