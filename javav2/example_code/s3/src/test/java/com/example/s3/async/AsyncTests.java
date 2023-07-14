package com.example.s3.async;

import com.example.s3.util.AsyncExampleUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.UUID;

class AsyncTests {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTests.class);
    private String bucketName ;
    private String key;

    @BeforeEach
    void setUp() {
        bucketName = "x-" + UUID.randomUUID();
        key = UUID.randomUUID().toString();
        AsyncExampleUtils.createBucket(bucketName);
    }

    @AfterEach
    void tearDown() {
        AsyncExampleUtils.deleteObject(bucketName, key);
        AsyncExampleUtils.deleteBucket(bucketName);
    }

    @Test
    @Tag("IntegrationTest")
    void putObjectFromStream() {
        PutObjectFromStreamAsync example = new PutObjectFromStreamAsync();
        PutObjectResponse putObjectResponse = example.putObjectFromStream(AsyncExampleUtils.client, bucketName, key);
        Assertions.assertNotNull(putObjectResponse.eTag());
    }
}