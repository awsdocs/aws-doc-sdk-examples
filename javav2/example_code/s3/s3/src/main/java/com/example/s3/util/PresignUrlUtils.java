// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.s3.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.File;

public class PresignUrlUtils {
    private static final Logger logger = LoggerFactory.getLogger(PresignUrlUtils.class);

    public static void createBucket(String bucketName, S3Client s3Client) {
        s3Client.createBucket(b -> b.bucket(bucketName));
        try (S3Waiter waiter = s3Client.waiter()) {
            waiter.waitUntilBucketExists(b -> b.bucket(bucketName));
        }
        logger.info("Bucket [{}] created", bucketName);
    }

    public static void deleteBucket(String bucketName, S3Client s3Client) {
        s3Client.deleteBucket(b -> b.bucket(bucketName));
        try (S3Waiter waiter = s3Client.waiter()) {
            waiter.waitUntilBucketNotExists(b -> b.bucket(bucketName));
        }
        logger.info("Bucket [{}] deleted", bucketName);
    }

    public static void deleteObject(String bucketName, String key, S3Client s3Client) {
        s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
        try (S3Waiter waiter = s3Client.waiter()) {
            waiter.waitUntilObjectNotExists(b -> b.bucket(bucketName).key(key));
        }
        logger.info("Object [{}] deleted", key);
    }

    public static void uploadFile(S3Client s3Client, String bucketName, String key, File file) {
        s3Client.putObject(b -> b.bucket(bucketName).key(key), file.toPath());
        try (S3Waiter waiter = S3Waiter.builder().client(s3Client).build() ){
            waiter.waitUntilObjectExists(w -> w.bucket(bucketName).key(key));
        }
        logger.info("File uploaded successfully");
    }
}
