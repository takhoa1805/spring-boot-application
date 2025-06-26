package com.lreas.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: grpc_quiz_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GrpcQuizServiceGrpc {

  private GrpcQuizServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.lreas.grpc.GrpcQuizService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc,
      com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc> getCreateQuizMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "createQuiz",
      requestType = com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc.class,
      responseType = com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc,
      com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc> getCreateQuizMethod() {
    io.grpc.MethodDescriptor<com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc, com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc> getCreateQuizMethod;
    if ((getCreateQuizMethod = GrpcQuizServiceGrpc.getCreateQuizMethod) == null) {
      synchronized (GrpcQuizServiceGrpc.class) {
        if ((getCreateQuizMethod = GrpcQuizServiceGrpc.getCreateQuizMethod) == null) {
          GrpcQuizServiceGrpc.getCreateQuizMethod = getCreateQuizMethod =
              io.grpc.MethodDescriptor.<com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc, com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "createQuiz"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc.getDefaultInstance()))
              .setSchemaDescriptor(new GrpcQuizServiceMethodDescriptorSupplier("createQuiz"))
              .build();
        }
      }
    }
    return getCreateQuizMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc,
      com.google.protobuf.Empty> getUpdateQuizMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "updateQuiz",
      requestType = com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc,
      com.google.protobuf.Empty> getUpdateQuizMethod() {
    io.grpc.MethodDescriptor<com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc, com.google.protobuf.Empty> getUpdateQuizMethod;
    if ((getUpdateQuizMethod = GrpcQuizServiceGrpc.getUpdateQuizMethod) == null) {
      synchronized (GrpcQuizServiceGrpc.class) {
        if ((getUpdateQuizMethod = GrpcQuizServiceGrpc.getUpdateQuizMethod) == null) {
          GrpcQuizServiceGrpc.getUpdateQuizMethod = getUpdateQuizMethod =
              io.grpc.MethodDescriptor.<com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "updateQuiz"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new GrpcQuizServiceMethodDescriptorSupplier("updateQuiz"))
              .build();
        }
      }
    }
    return getUpdateQuizMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GrpcQuizServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcQuizServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcQuizServiceStub>() {
        @java.lang.Override
        public GrpcQuizServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcQuizServiceStub(channel, callOptions);
        }
      };
    return GrpcQuizServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static GrpcQuizServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcQuizServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcQuizServiceBlockingV2Stub>() {
        @java.lang.Override
        public GrpcQuizServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcQuizServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return GrpcQuizServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GrpcQuizServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcQuizServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcQuizServiceBlockingStub>() {
        @java.lang.Override
        public GrpcQuizServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcQuizServiceBlockingStub(channel, callOptions);
        }
      };
    return GrpcQuizServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GrpcQuizServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcQuizServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcQuizServiceFutureStub>() {
        @java.lang.Override
        public GrpcQuizServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcQuizServiceFutureStub(channel, callOptions);
        }
      };
    return GrpcQuizServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void createQuiz(com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc request,
        io.grpc.stub.StreamObserver<com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateQuizMethod(), responseObserver);
    }

    /**
     */
    default void updateQuiz(com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateQuizMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service GrpcQuizService.
   */
  public static abstract class GrpcQuizServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return GrpcQuizServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service GrpcQuizService.
   */
  public static final class GrpcQuizServiceStub
      extends io.grpc.stub.AbstractAsyncStub<GrpcQuizServiceStub> {
    private GrpcQuizServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcQuizServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcQuizServiceStub(channel, callOptions);
    }

    /**
     */
    public void createQuiz(com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc request,
        io.grpc.stub.StreamObserver<com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateQuizMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateQuiz(com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateQuizMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service GrpcQuizService.
   */
  public static final class GrpcQuizServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<GrpcQuizServiceBlockingV2Stub> {
    private GrpcQuizServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcQuizServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcQuizServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc createQuiz(com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateQuizMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty updateQuiz(com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateQuizMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service GrpcQuizService.
   */
  public static final class GrpcQuizServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GrpcQuizServiceBlockingStub> {
    private GrpcQuizServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcQuizServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcQuizServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc createQuiz(com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateQuizMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty updateQuiz(com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateQuizMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service GrpcQuizService.
   */
  public static final class GrpcQuizServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<GrpcQuizServiceFutureStub> {
    private GrpcQuizServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcQuizServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcQuizServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc> createQuiz(
        com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateQuizMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> updateQuiz(
        com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateQuizMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_QUIZ = 0;
  private static final int METHODID_UPDATE_QUIZ = 1;

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
        case METHODID_CREATE_QUIZ:
          serviceImpl.createQuiz((com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc) request,
              (io.grpc.stub.StreamObserver<com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc>) responseObserver);
          break;
        case METHODID_UPDATE_QUIZ:
          serviceImpl.updateQuiz((com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
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
          getCreateQuizMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc,
              com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc>(
                service, METHODID_CREATE_QUIZ)))
        .addMethod(
          getUpdateQuizMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc,
              com.google.protobuf.Empty>(
                service, METHODID_UPDATE_QUIZ)))
        .build();
  }

  private static abstract class GrpcQuizServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GrpcQuizServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.lreas.grpc.GrpcQuizServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GrpcQuizService");
    }
  }

  private static final class GrpcQuizServiceFileDescriptorSupplier
      extends GrpcQuizServiceBaseDescriptorSupplier {
    GrpcQuizServiceFileDescriptorSupplier() {}
  }

  private static final class GrpcQuizServiceMethodDescriptorSupplier
      extends GrpcQuizServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    GrpcQuizServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (GrpcQuizServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GrpcQuizServiceFileDescriptorSupplier())
              .addMethod(getCreateQuizMethod())
              .addMethod(getUpdateQuizMethod())
              .build();
        }
      }
    }
    return result;
  }
}
