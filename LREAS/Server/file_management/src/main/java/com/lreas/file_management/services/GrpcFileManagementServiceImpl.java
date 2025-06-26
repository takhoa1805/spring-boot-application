package com.lreas.file_management.services;

import com.lreas.file_management.dtos.NewDocumentsResponse;
import com.lreas.grpc.GrpcFileManagementServiceGrpc;
import com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc;
import com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc;

import io.grpc.stub.StreamObserver;

import net.devh.boot.grpc.server.service.GrpcService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

@GrpcService
public class GrpcFileManagementServiceImpl extends GrpcFileManagementServiceGrpc.GrpcFileManagementServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(GrpcFileManagementServiceImpl.class);

    private final FileManagementService fileManagementService;

    public GrpcFileManagementServiceImpl(
            @Qualifier("fileManagementServiceImpl") FileManagementService fileManagementService
    ) {
        this.fileManagementService = fileManagementService;
    }

    @Override
    public void createDocument(
            NewDocumentsGrpc newDocumentsGrpc,
            StreamObserver<NewDocumentsInfoGrpc> responseObserver
    ) {
        try {
            NewDocumentsResponse newDocumentsResponse = this.fileManagementService.createDocument(
                    newDocumentsGrpc.getParentId().isEmpty() ? null : newDocumentsGrpc.getParentId(),
                    newDocumentsGrpc.getName().isEmpty() ? null : newDocumentsGrpc.getName(),
                    newDocumentsGrpc.getType().isEmpty() ? null : newDocumentsGrpc.getType(),
                    newDocumentsGrpc.getUserId().isEmpty() ? null : newDocumentsGrpc.getUserId()
            );

            // convert data
            NewDocumentsInfoGrpc newDocumentsInfoGrpc = this.convertToNewDocumentsInfoGrpc(
                    newDocumentsResponse
            );

            // return
            responseObserver.onNext(newDocumentsInfoGrpc);
            responseObserver.onCompleted();
        }
        catch (Exception e) {
            logger.error("Grpc Create Document: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    private NewDocumentsInfoGrpc convertToNewDocumentsInfoGrpc(
            NewDocumentsResponse newDocumentsResponse
    ) {
        NewDocumentsInfoGrpc.Builder builder = NewDocumentsInfoGrpc.newBuilder();

        Optional.ofNullable(newDocumentsResponse.getSuccess()).ifPresent(builder::setSuccess);
        Optional.ofNullable(newDocumentsResponse.getMessage()).ifPresent(builder::setMessage);
        Optional.ofNullable(newDocumentsResponse.getResourceId()).ifPresent(builder::setResourceId);
        Optional.ofNullable(newDocumentsResponse.getName()).ifPresent(builder::setName);
        Optional.ofNullable(newDocumentsResponse.getType()).ifPresent(builder::setType);
        Optional.ofNullable(newDocumentsResponse.getOwnerId()).ifPresent(builder::setOwnerId);
        Optional.ofNullable(newDocumentsResponse.getMongoId()).ifPresent(builder::setMongoId);

        return builder.build();
    }
}
