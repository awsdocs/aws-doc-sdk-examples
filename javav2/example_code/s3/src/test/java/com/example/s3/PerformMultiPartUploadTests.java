// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.s3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.UUID;

class PerformMultiPartUploadTests {
    private final S3Client s3Client = PerformMultiPartUpload.s3Client;
    private String bucketName;
    private String key;
    private PerformMultiPartUpload performMultiPartUpload;

    @BeforeEach
    void setUp() {
        bucketName = "test-bucket-" + UUID.randomUUID();
        key = UUID.randomUUID().toString();
        performMultiPartUpload = new PerformMultiPartUpload();
        PerformMultiPartUpload.createBucket(bucketName);
    }

    @AfterEach
    void tearDown() {
        PerformMultiPartUpload.deleteResources(bucketName, key);
    }

    @Test
    @Tag("IntegrationTest")
    void multipartUploadWithTransferManagerTest() {
        try {
            performMultiPartUpload.multipartUploadWithTransferManager(bucketName, key, PerformMultiPartUpload.filePath);
            GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).partNumber(1)).response();
            Assertions.assertTrue(response.partsCount() > 1);
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    @Tag("IntegrationTest")
    void multipartUploadWithS3AsyncClientTest() {
        try {
            performMultiPartUpload.multipartUploadWithS3AsyncClient(bucketName, key, PerformMultiPartUpload.filePath);
            GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).partNumber(1)).response();
            Assertions.assertTrue(response.partsCount() > 1);
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    @Tag("IntegrationTest")
    void multipartUploadWithS3ClientTest() {
        try {
            performMultiPartUpload.multipartUploadWithS3Client(bucketName, key, PerformMultiPartUpload.filePath);
            GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).partNumber(1)).response();
            Assertions.assertTrue(response.partsCount() > 1);
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        }
    }
}
