package com.lreas.profile.services;

import com.lreas.grpc.GrpcProfileServiceGrpc;
import com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc;
import com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc;

import com.lreas.profile.dtos.UserInfoDto;

import io.grpc.stub.StreamObserver;

import net.devh.boot.grpc.server.service.GrpcService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@GrpcService
public class GrpcProfileServiceImpl extends GrpcProfileServiceGrpc.GrpcProfileServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(GrpcProfileServiceImpl.class);

    private final ProfileService profileService;

    @Autowired
    public GrpcProfileServiceImpl(
            ProfileService profileService
    ) {
        this.profileService = profileService;
    }

    @Override
    public void getUserInfo(
            GetUserInfoGrpc request,
            StreamObserver<UserInfoGrpc> responseObserver
    ) {
        try {
            UserInfoDto userInfoDto = this.profileService.getUserInfo(request.getUserId());

            // convert data
            UserInfoGrpc userInfoGrpc = this.convertToUserInfoGrpc(userInfoDto);

            // return
            responseObserver.onNext(userInfoGrpc);
            responseObserver.onCompleted();
        }
        catch (Exception ex) {
            logger.error("Grpc Get User Info: {}", ex.getMessage());
            responseObserver.onError(ex);
        }
    }

    private UserInfoGrpc convertToUserInfoGrpc(
            UserInfoDto userInfoDto
    ) {
        UserInfoGrpc.Builder builder = UserInfoGrpc.newBuilder();
        Optional.ofNullable(userInfoDto.avtPath).ifPresent(builder::setAvtPath);
        Optional.ofNullable(userInfoDto.username).ifPresent(builder::setUserName);
        Optional.ofNullable(userInfoDto.birthday).ifPresent(t -> builder.setBirthday(String.valueOf(userInfoDto.birthday.getTime())));
        Optional.ofNullable(userInfoDto.gender).ifPresent(t -> builder.setGender(String.valueOf(t)));
        Optional.ofNullable(userInfoDto.otherGender).ifPresent(builder::setOtherGender);
        Optional.ofNullable(userInfoDto.description).ifPresent(builder::setDescription);
        Optional.ofNullable(userInfoDto.email).ifPresent(builder::setEmail);
        Optional.ofNullable(userInfoDto.phone).ifPresent(builder::setPhone);
        Optional.ofNullable(userInfoDto.address).ifPresent(builder::setAddress);
        Optional.ofNullable(userInfoDto.institutionName).ifPresent(builder::setInstitutionName);
        Optional.ofNullable(userInfoDto.invitationCode).ifPresent(builder::setInvitationCode);

        return builder.build();
    }
}
