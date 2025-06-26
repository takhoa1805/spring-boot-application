package com.lreas.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: grpc_profile_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GrpcProfileServiceGrpc {

  private GrpcProfileServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.lreas.grpc.GrpcProfileService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc,
      com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc> getGetUserInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getUserInfo",
      requestType = com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc.class,
      responseType = com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc,
      com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc> getGetUserInfoMethod() {
    io.grpc.MethodDescriptor<com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc, com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc> getGetUserInfoMethod;
    if ((getGetUserInfoMethod = GrpcProfileServiceGrpc.getGetUserInfoMethod) == null) {
      synchronized (GrpcProfileServiceGrpc.class) {
        if ((getGetUserInfoMethod = GrpcProfileServiceGrpc.getGetUserInfoMethod) == null) {
          GrpcProfileServiceGrpc.getGetUserInfoMethod = getGetUserInfoMethod =
              io.grpc.MethodDescriptor.<com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc, com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getUserInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc.getDefaultInstance()))
              .setSchemaDescriptor(new GrpcProfileServiceMethodDescriptorSupplier("getUserInfo"))
              .build();
        }
      }
    }
    return getGetUserInfoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GrpcProfileServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcProfileServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcProfileServiceStub>() {
        @java.lang.Override
        public GrpcProfileServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcProfileServiceStub(channel, callOptions);
        }
      };
    return GrpcProfileServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static GrpcProfileServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcProfileServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcProfileServiceBlockingV2Stub>() {
        @java.lang.Override
        public GrpcProfileServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcProfileServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return GrpcProfileServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GrpcProfileServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcProfileServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcProfileServiceBlockingStub>() {
        @java.lang.Override
        public GrpcProfileServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcProfileServiceBlockingStub(channel, callOptions);
        }
      };
    return GrpcProfileServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GrpcProfileServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcProfileServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcProfileServiceFutureStub>() {
        @java.lang.Override
        public GrpcProfileServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcProfileServiceFutureStub(channel, callOptions);
        }
      };
    return GrpcProfileServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getUserInfo(com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc request,
        io.grpc.stub.StreamObserver<com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserInfoMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service GrpcProfileService.
   */
  public static abstract class GrpcProfileServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return GrpcProfileServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service GrpcProfileService.
   */
  public static final class GrpcProfileServiceStub
      extends io.grpc.stub.AbstractAsyncStub<GrpcProfileServiceStub> {
    private GrpcProfileServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcProfileServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcProfileServiceStub(channel, callOptions);
    }

    /**
     */
    public void getUserInfo(com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc request,
        io.grpc.stub.StreamObserver<com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserInfoMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service GrpcProfileService.
   */
  public static final class GrpcProfileServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<GrpcProfileServiceBlockingV2Stub> {
    private GrpcProfileServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcProfileServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcProfileServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc getUserInfo(com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserInfoMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service GrpcProfileService.
   */
  public static final class GrpcProfileServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GrpcProfileServiceBlockingStub> {
    private GrpcProfileServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcProfileServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcProfileServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc getUserInfo(com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserInfoMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service GrpcProfileService.
   */
  public static final class GrpcProfileServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<GrpcProfileServiceFutureStub> {
    private GrpcProfileServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcProfileServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcProfileServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc> getUserInfo(
        com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserInfoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_USER_INFO = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_USER_INFO:
          serviceImpl.getUserInfo((com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc) request,
              (io.grpc.stub.StreamObserver<com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetUserInfoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.lreas.grpc.GrpcProfileServiceOuterClass.GetUserInfoGrpc,
              com.lreas.grpc.GrpcProfileServiceOuterClass.UserInfoGrpc>(
                service, METHODID_GET_USER_INFO)))
        .build();
  }

  private static abstract class GrpcProfileServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GrpcProfileServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.lreas.grpc.GrpcProfileServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GrpcProfileService");
    }
  }

  private static final class GrpcProfileServiceFileDescriptorSupplier
      extends GrpcProfileServiceBaseDescriptorSupplier {
    GrpcProfileServiceFileDescriptorSupplier() {}
  }

  private static final class GrpcProfileServiceMethodDescriptorSupplier
      extends GrpcProfileServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    GrpcProfileServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GrpcProfileServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GrpcProfileServiceFileDescriptorSupplier())
              .addMethod(getGetUserInfoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
