package com.lreas.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: grpc_file_management_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GrpcFileManagementServiceGrpc {

  private GrpcFileManagementServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.lreas.grpc.GrpcFileManagementService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc,
      com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc> getCreateDocumentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "createDocument",
      requestType = com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc.class,
      responseType = com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc,
      com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc> getCreateDocumentMethod() {
    io.grpc.MethodDescriptor<com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc, com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc> getCreateDocumentMethod;
    if ((getCreateDocumentMethod = GrpcFileManagementServiceGrpc.getCreateDocumentMethod) == null) {
      synchronized (GrpcFileManagementServiceGrpc.class) {
        if ((getCreateDocumentMethod = GrpcFileManagementServiceGrpc.getCreateDocumentMethod) == null) {
          GrpcFileManagementServiceGrpc.getCreateDocumentMethod = getCreateDocumentMethod =
              io.grpc.MethodDescriptor.<com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc, com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "createDocument"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc.getDefaultInstance()))
              .setSchemaDescriptor(new GrpcFileManagementServiceMethodDescriptorSupplier("createDocument"))
              .build();
        }
      }
    }
    return getCreateDocumentMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GrpcFileManagementServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcFileManagementServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcFileManagementServiceStub>() {
        @java.lang.Override
        public GrpcFileManagementServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcFileManagementServiceStub(channel, callOptions);
        }
      };
    return GrpcFileManagementServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static GrpcFileManagementServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcFileManagementServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcFileManagementServiceBlockingV2Stub>() {
        @java.lang.Override
        public GrpcFileManagementServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcFileManagementServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return GrpcFileManagementServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GrpcFileManagementServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcFileManagementServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcFileManagementServiceBlockingStub>() {
        @java.lang.Override
        public GrpcFileManagementServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcFileManagementServiceBlockingStub(channel, callOptions);
        }
      };
    return GrpcFileManagementServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GrpcFileManagementServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GrpcFileManagementServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GrpcFileManagementServiceFutureStub>() {
        @java.lang.Override
        public GrpcFileManagementServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GrpcFileManagementServiceFutureStub(channel, callOptions);
        }
      };
    return GrpcFileManagementServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void createDocument(com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc request,
        io.grpc.stub.StreamObserver<com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateDocumentMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service GrpcFileManagementService.
   */
  public static abstract class GrpcFileManagementServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return GrpcFileManagementServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service GrpcFileManagementService.
   */
  public static final class GrpcFileManagementServiceStub
      extends io.grpc.stub.AbstractAsyncStub<GrpcFileManagementServiceStub> {
    private GrpcFileManagementServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcFileManagementServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcFileManagementServiceStub(channel, callOptions);
    }

    /**
     */
    public void createDocument(com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc request,
        io.grpc.stub.StreamObserver<com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateDocumentMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service GrpcFileManagementService.
   */
  public static final class GrpcFileManagementServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<GrpcFileManagementServiceBlockingV2Stub> {
    private GrpcFileManagementServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcFileManagementServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcFileManagementServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc createDocument(com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateDocumentMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service GrpcFileManagementService.
   */
  public static final class GrpcFileManagementServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GrpcFileManagementServiceBlockingStub> {
    private GrpcFileManagementServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcFileManagementServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcFileManagementServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc createDocument(com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateDocumentMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service GrpcFileManagementService.
   */
  public static final class GrpcFileManagementServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<GrpcFileManagementServiceFutureStub> {
    private GrpcFileManagementServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrpcFileManagementServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GrpcFileManagementServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc> createDocument(
        com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateDocumentMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_DOCUMENT = 0;

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
        case METHODID_CREATE_DOCUMENT:
          serviceImpl.createDocument((com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc) request,
              (io.grpc.stub.StreamObserver<com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc>) responseObserver);
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
          getCreateDocumentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsGrpc,
              com.lreas.grpc.GrpcFileManagementServiceOuterClass.NewDocumentsInfoGrpc>(
                service, METHODID_CREATE_DOCUMENT)))
        .build();
  }

  private static abstract class GrpcFileManagementServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GrpcFileManagementServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.lreas.grpc.GrpcFileManagementServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GrpcFileManagementService");
    }
  }

  private static final class GrpcFileManagementServiceFileDescriptorSupplier
      extends GrpcFileManagementServiceBaseDescriptorSupplier {
    GrpcFileManagementServiceFileDescriptorSupplier() {}
  }

  private static final class GrpcFileManagementServiceMethodDescriptorSupplier
      extends GrpcFileManagementServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    GrpcFileManagementServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (GrpcFileManagementServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GrpcFileManagementServiceFileDescriptorSupplier())
              .addMethod(getCreateDocumentMethod())
              .build();
        }
      }
    }
    return result;
  }
}
