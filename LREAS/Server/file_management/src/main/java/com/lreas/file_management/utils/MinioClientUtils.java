package com.lreas.file_management.utils;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class MinioClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(MinioClientUtils.class);
    private final MinioClient minioClient;
    private final String bucket;

    private final long PART_SIZE = 5242880; // byte

    @Autowired
    public MinioClientUtils(
            MinioClientConfig minioClientConfig
    ) throws Exception {
        try {
            this.bucket = minioClientConfig.getBucket();
            logger.info("Bucket name: {}", this.bucket);

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
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(this.bucket)
                                .build()
                );
            }
        } catch (Exception e) {
            logger.error("Error initializing MinioClientUtils", e);
            throw e;
        }
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
            long size, String contentType
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

    public void assertExistence(
            String objectName
    ) throws Exception {
        this.minioClient.statObject(
                StatObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .build()
        );
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

    public void downloadObject(
            String objectName, String fileName
    ) throws Exception {
        this.minioClient.downloadObject(
                DownloadObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .filename(fileName)
                        .build()
        );
    }

    public StatObjectResponse getStatObject(
            String objectName
    ) throws Exception {
        return this.minioClient.statObject(
                StatObjectArgs
                        .builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .build()
        );
    }
}