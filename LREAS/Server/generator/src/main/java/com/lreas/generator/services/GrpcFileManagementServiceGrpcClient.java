package com.lreas.generator.services;

import com.lreas.generator.dtos.NewDocumentsRequest;
import com.lreas.generator.dtos.NewDocumentsResponse;

import com.lreas.grpc.GrpcFileManagementServiceGrpc;
import com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc;
import com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc;

import lombok.Setter;

import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Setter
@Service
public class GrpcFileManagementServiceGrpcClient {
    @GrpcClient("grpc_file_management_service")
    private GrpcFileManagementServiceGrpc.GrpcFileManagementServiceBlockingStub grpcFileManagementServiceBlockingStub;

    public NewDocumentsResponse createDocument(
            NewDocumentsRequest request
    ) {
        NewDocumentsGrpc.Builder newDocumentsGrpcBuilder = NewDocumentsGrpc.newBuilder();
        Optional.ofNullable(request.parentId).ifPresent(newDocumentsGrpcBuilder::setParentId);
        Optional.ofNullable(request.name).ifPresent(newDocumentsGrpcBuilder::setName);
        Optional.ofNullable(request.type).ifPresent(newDocumentsGrpcBuilder::setType);
        Optional.ofNullable(request.userId).ifPresent(newDocumentsGrpcBuilder::setUserId);

        NewDocumentsInfoGrpc response = this.grpcFileManagementServiceBlockingStub.createDocument(
                newDocumentsGrpcBuilder.build()
        );

        return this.convertToNewDocumentsResponse(response);
    }

    private NewDocumentsResponse convertToNewDocumentsResponse(
            NewDocumentsInfoGrpc response
    ) {
        if (response == null) {
            return null;
        }

        NewDocumentsResponse newDocumentsResponse = new NewDocumentsResponse();

        newDocumentsResponse.success = response.getSuccess();
        newDocumentsResponse.message = response.getMessage().isEmpty() ? null : response.getMessage();
        newDocumentsResponse.resourceId = response.getResourceId().isEmpty() ? null : response.getResourceId();
        newDocumentsResponse.parentId = response.getParentId().isEmpty() ? null : response.getParentId();
        newDocumentsResponse.name = response.getName().isEmpty() ? null : response.getName();
        newDocumentsResponse.type = response.getType().isEmpty() ? null : response.getType();
        newDocumentsResponse.ownerId = response.getOwnerId().isEmpty() ? null : response.getOwnerId();
        newDocumentsResponse.mongoId = response.getMongoId().isEmpty() ? null : response.getMongoId();

        return newDocumentsResponse;
    }
}
