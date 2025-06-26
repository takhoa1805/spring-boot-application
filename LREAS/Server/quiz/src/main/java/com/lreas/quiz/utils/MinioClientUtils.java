package com.lreas.quiz.utils;

import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Map;

@Component
public class MinioClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(MinioClientUtils.class);
    private final MinioClient minioClient;
    private final String bucket;

    private final long PART_SIZE = 5242880; // 5MB

    @Autowired
    public MinioClientUtils(
            MinioClientConfig minioClientConfig
    ) throws Exception {
        try {
            this.bucket = minioClientConfig.getBucket();

            String accessKey = minioClientConfig.getAccessKey();
            String secretKey = minioClientConfig.getSecretKey();

            this.minioClient = MinioClient.builder()
                    .endpoint(minioClientConfig.getUrl())
                    .credentials(accessKey, secretKey)
                    .build();

            boolean found = this.minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(this.bucket)
                            .build()
            );
            if (!found) {
                logger.info("Bucket not found. Creating new bucket...");
                this.minioClient.makeBucket(
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
            logger.error("Error initializing MinioClientUtils: {}", e.getMessage());
            throw e;
        }
    }

    public enum FILE_STATUS {
        AVAILABLE, DELETED
    }

    private void uploadSmallFile(
            String objectName, InputStream fileInputStream,
            long size, String contentType
    ) throws Exception {
        this.minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .stream(fileInputStream, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    public void uploadFile(
            String objectName, InputStream fileInputStream,
            long size, String contentType,
            Map<String, String> metadata
    ) throws Exception {
        long numParts = (size - 1) / this.PART_SIZE + 1;
        ComposeSource[] composeSources = new ComposeSource[(int) numParts];
        Thread[] threads = new Thread[(int) numParts];
        List<String> deleteObjects = new LinkedList<>();
        byte[] buffer = fileInputStream.readAllBytes();

        for (int i = 0; i < numParts; i++) {
            final int finalI = i;

            // thread for uploading file
            threads[i] = new Thread(() -> {
                try {
                    long currSize = this.PART_SIZE;
                    if (finalI == threads.length - 1) {
                        currSize = size - (finalI * this.PART_SIZE);
                    }

                    // get buffer in range
                    byte[] currBuffer = Arrays.copyOfRange(
                            buffer,
                            (int) (finalI * this.PART_SIZE),
                            (int) ((finalI + 1) * this.PART_SIZE)
                    );
                    String currObjectName = objectName + "_" + finalI;

                    // logging
                    logger.debug("{}: {}. Size: {}", Thread.currentThread().getName(), "Started", currSize);

                    this.uploadSmallFile(
                            currObjectName,
                            new ByteArrayInputStream(currBuffer),
                            currSize, contentType
                    );

                    ComposeSource composeSource = ComposeSource
                            .builder()
                            .bucket(this.bucket)
                            .object(currObjectName)
                            .build();
                    composeSources[finalI] = composeSource;

                    // add to delete object
                    deleteObjects.add(currObjectName);
                }
                catch (Exception e) {
                    logger.error("{}: {}", Thread.currentThread().getName(), e.getMessage());
                }
            });
            threads[i].setName("Upload Thread-" + i);
            threads[i].start();
        }

        // wait for all threads to be finished
        for (Thread thread : threads) {
            thread.join();
        }

        // compose different parts
        this.minioClient.composeObject(
                ComposeObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .userMetadata(metadata)
                        .sources(Arrays.asList(composeSources))
                        .build()
        );

        // delete all chunk objects in a separate thread
        Thread deleteThread = new Thread(() -> {
            for (String deleteObject : deleteObjects) {
                try {
                    this.deleteFile(deleteObject);
                } catch (Exception e) {
                    logger.error("{}: {}", Thread.currentThread().getName(), e.getMessage());
                }
            }
        });
        deleteThread.setName("Delete Thread");
        deleteThread.start();
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