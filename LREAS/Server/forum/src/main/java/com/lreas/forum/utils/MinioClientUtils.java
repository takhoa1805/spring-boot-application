package com.lreas.forum.utils;

import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.Map;

@Component
public class MinioClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(MinioClientUtils.class);
    private final MinioClient minioClient;
    private final String bucket;

    @Autowired
    public MinioClientUtils(
            MinioClientConfig minioClientConfig
    ) throws Exception {
        try {
            this.bucket = minioClientConfig.getBucket();

            String accessKey = minioClientConfig.getAccessKey();
            String secretKey = minioClientConfig.getSecretKey();

            minioClient = MinioClient.builder()
                    .endpoint(minioClientConfig.getUrl())
                    .credentials(accessKey, secretKey)
                    .build();

            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(this.bucket)
                            .build()
            );
            if (!found) {
                logger.info("Bucket not found. Creating new bucket...");
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(this.bucket)
                                .build()
                );
                logger.info("Created bucket: {}", this.bucket);
            }
            else {
                logger.info("Connected to bucket: {}", this.bucket);
            }
        } catch (Exception e) {
            logger.error("Error initializing MinioClientUtils", e);
            throw e;
        }
    }

    public void uploadFile(
            String objectName, InputStream fileInputStream,
            long size, String contentType,
            Map<String, String> metadata
    ) throws Exception {
        this.minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .stream(fileInputStream, size, -1)
                        .userMetadata(metadata)
                        .contentType(contentType)
                        .build()
        );
    }

    public static enum FILE_STATUS {
        AVAILABLE, DELETED
    }

    public InputStream getFile(String objectName) throws Exception {
        return this.minioClient.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .build()
        );
    }

    public void deleteFile(String objectName) throws Exception {
        this.minioClient.removeObject(
                RemoveObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .build()
        );
    }

    public String getUrl(String objectName) throws Exception {
        return this.minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs
                        .builder()
                        .method(Method.GET)
                        .bucket(this.bucket)
                        .object(objectName)
                        .expiry(2, TimeUnit.HOURS)
                        .build()
        );
    }

    public void cloneObject(
            String objectName, String newObjectName
    ) throws Exception {
        this.minioClient.copyObject(
                CopyObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .source(
                                CopySource
                                        .builder()
                                        .bucket(this.bucket)
                                        .object(newObjectName)
                                        .build()
                        )
                        .build()
        );
    }

    public Map<String, String> getMetadata(
            String objectName
    ) throws Exception {
        StatObjectResponse objectStat = minioClient.statObject(
                StatObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .build()
        );
        return objectStat.userMetadata();
    }

    public void setTags(
            Map<String, String> tags,
            String objectName
    ) throws Exception {
        this.minioClient.setObjectTags(
                SetObjectTagsArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .tags(tags)
                        .build()
        );
    }

    public Map<String, String> getTags(
            String objectName
    ) throws Exception {
        return this.minioClient.getObjectTags(
                GetObjectTagsArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .build()
        ).get();
    }
}