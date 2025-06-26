package com.lreas.quiz.services;

import com.lreas.grpc.GrpcProfileServiceGrpc;
import com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc;
import com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc;

import com.lreas.quiz.dtos.UserInfoDto;

import com.lreas.quiz.models.User;
import lombok.Setter;

import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.stereotype.Service;

import java.util.Date;

@Setter
@Service
public class GrpcProfileServiceGrpcClient {
    @GrpcClient("grpc_profile_service")
    private GrpcProfileServiceGrpc.GrpcProfileServiceBlockingStub blockingStub;

    public UserInfoDto getUserInfo(String userId) {
        GetUserInfoGrpc getUserInfoGrpc = GetUserInfoGrpc.newBuilder()
                .setUserId(userId == null ? "" : userId)
                .build();

        // send request
        UserInfoGrpc response = this.blockingStub.getUserInfo(getUserInfoGrpc);

        return this.convertToUserInfoDto(response);
    }

    private UserInfoDto convertToUserInfoDto(
            UserInfoGrpc userInfoGrpc
    ) {
        UserInfoDto userInfoDto = new UserInfoDto();

        userInfoDto.avtPath = userInfoGrpc.getAvtPath();
        userInfoDto.username = userInfoGrpc.getUserName();
        userInfoDto.birthday = userInfoGrpc.getBirthday().isEmpty() ? null : new Date(Long.parseLong(userInfoGrpc.getBirthday()));
        userInfoDto.gender = userInfoGrpc.getGender().isEmpty() ? null : User.GENDER.valueOf(userInfoGrpc.getGender());
        userInfoDto.otherGender = userInfoGrpc.getOtherGender();
        userInfoDto.description = userInfoGrpc.getDescription();
        userInfoDto.email = userInfoGrpc.getEmail();
        userInfoDto.phone = userInfoGrpc.getPhone();
        userInfoDto.address = userInfoGrpc.getAddress();
        userInfoDto.institutionName = userInfoGrpc.getInstitutionName();
        userInfoDto.invitationCode = userInfoGrpc.getInvitationCode();

        return userInfoDto;
    }
}
