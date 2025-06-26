package com.lreas.profile.dtos;

import com.lreas.profile.models.User;
import com.lreas.profile.utils.MinioClientUtils;
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
            if (user.getAvtPath() != null) {
                this.avtPath = minioClientUtils.getUrl(user.getAvtPath());
            }
            else {
                this.avtPath = "";
            }
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
