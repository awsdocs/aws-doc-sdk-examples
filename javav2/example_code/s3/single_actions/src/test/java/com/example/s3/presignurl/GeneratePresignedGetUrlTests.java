// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.presignurl;

import com.example.s3.GeneratePresignedGetUrlAndRetrieve;
import com.example.s3.GeneratePresignedUrlAndPutFileWithMetadata;
import com.example.s3.util.PresignUrlUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.util.UUID;

class GeneratePresignedGetUrlTests {
    private static final String BUCKET_NAME = "b-" + UUID.randomUUID();
    private static final String KEY_NAME = "k-" + UUID.randomUUID();
    private static final S3Client s3Client = S3Client.create();
    private static final File PNG_FILE = GeneratePresignedUrlAndPutFileWithMetadata.getFileForForClasspathResource("image.png");


    @BeforeAll
    static void beforeAll() {
        PresignUrlUtils.createBucket(BUCKET_NAME, s3Client);
        PresignUrlUtils.uploadFile(s3Client, BUCKET_NAME, KEY_NAME, PNG_FILE);
    }

    @AfterAll
    static void afterAll() {
        PresignUrlUtils.deleteObject(BUCKET_NAME, KEY_NAME, s3Client);
        PresignUrlUtils.deleteBucket(BUCKET_NAME, s3Client);
    }

    @Test
    @Tag("IntegrationTest")
    void testCreatePresignedGetUrl() {
        GeneratePresignedGetUrlAndRetrieve presignInstanceUnderTest = new GeneratePresignedGetUrlAndRetrieve();

        final String presignedUrlString = presignInstanceUnderTest.createPresignedGetUrl(BUCKET_NAME, KEY_NAME);
        Assertions.assertTrue(presignedUrlString.contains(KEY_NAME));
    }

    @Test
    @Tag("IntegrationTest")
    void testUsingHttpUrlConnection() {
        GeneratePresignedGetUrlAndRetrieve presignInstanceUnderTest = new GeneratePresignedGetUrlAndRetrieve();
        final String presignedUrlString = presignInstanceUnderTest.createPresignedGetUrl(BUCKET_NAME, KEY_NAME);

        final byte[] bytes = presignInstanceUnderTest.useHttpUrlConnectionToGet(presignedUrlString);
        Assertions.assertTrue(bytes.length > 0);
    }

    @Test
    @Tag("IntegrationTest")
    void testUsingSdkHttpClient() {
        GeneratePresignedGetUrlAndRetrieve presignInstanceUnderTest = new GeneratePresignedGetUrlAndRetrieve();
        final String presignedUrlString = presignInstanceUnderTest.createPresignedGetUrl(BUCKET_NAME, KEY_NAME);

        final byte[] bytes = presignInstanceUnderTest.useHttpClientToGet(presignedUrlString);
        Assertions.assertTrue(bytes.length > 0);
    }

    @Test
    @Tag("IntegrationTest")
    void testUsingJdkHttpClient() {
        GeneratePresignedGetUrlAndRetrieve presignInstanceUnderTest = new GeneratePresignedGetUrlAndRetrieve();
        final String presignedUrlString = presignInstanceUnderTest.createPresignedGetUrl(BUCKET_NAME, KEY_NAME);

        final byte[] bytes = presignInstanceUnderTest.useSdkHttpClientToPut(presignedUrlString);
        Assertions.assertTrue(bytes.length > 0);
    }
}