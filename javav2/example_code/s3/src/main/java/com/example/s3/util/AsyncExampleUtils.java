package com.example.s3.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3AsyncClient;

public class AsyncExampleUtils {
    public static S3AsyncClient client = S3AsyncClient.crtCreate();
    private static final Logger logger = LoggerFactory.getLogger(AsyncExampleUtils.class);

    public static void createBucket(String bucketName) {
        client.createBucket(b -> b.bucket(bucketName)).join();
        client.waiter().waitUntilBucketExists(b -> b.bucket(bucketName)).join();
        logger.info("Bucket {} created.", bucketName);
    }

    public static void deleteObject(String bucketName, String key) {
        client.deleteObject(b -> b.bucket(bucketName).key(key)).join();
        logger.info("Object {} deleted from bucket {}.", key, bucketName);
    }

    public static void deleteBucket(String bucketName) {
        client.deleteBucket(b -> b.bucket(bucketName)).join();
        client.waiter().waitUntilBucketNotExists(b -> b.bucket(bucketName)).join();
        logger.info("Bucket {} deleted.", bucketName);
    }

    public static String randomString() {
        int length = (int)(Math.random()*100);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) (Math.random() * 26 + 97));
        }
        return sb.toString();
    }

}
