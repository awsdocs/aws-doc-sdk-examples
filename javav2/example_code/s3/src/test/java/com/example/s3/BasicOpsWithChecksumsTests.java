// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.s3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumMode;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectAttributes;
import software.amazon.awssdk.utils.BinaryUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

class BasicOpsWithChecksumsTests {
    private final S3Client s3Client = BasicOpsWithChecksums.s3Client;
    private String bucketName;
    private String key;
    private BasicOpsWithChecksums basicOpsWithChecksums;

    @BeforeEach
    void setUp() {
        bucketName = "test-bucket-" + UUID.randomUUID();
        key = UUID.randomUUID().toString();
        basicOpsWithChecksums = new BasicOpsWithChecksums();
        BasicOpsWithChecksums.createBucket(bucketName);
    }

    @AfterEach
    void tearDown() {
        BasicOpsWithChecksums.deleteResources(bucketName, key);
    }

    @Test
    @Tag("IntegrationTest")
    void putObjectWithChecksumTest() {
        basicOpsWithChecksums.putObjectWithChecksum(bucketName, key);

        GetObjectAttributesResponse objectAttributes = s3Client.getObjectAttributes(b -> b
                .bucket(bucketName)
                .key(key)
                .objectAttributes(ObjectAttributes.CHECKSUM));
        Assertions.assertNotNull(objectAttributes.checksum());
    }

    @Test
    @Tag("IntegrationTest")
    void getObjectWithChecksumTest() {
        String stringObjToUpload = "This is a test";
        String encodedChecksum = calculateChecksumForString(stringObjToUpload, Algorithm.CRC32);

        basicOpsWithChecksums.putObjectWithChecksum(bucketName, key);

        GetObjectResponse objectWithChecksum = basicOpsWithChecksums.getObjectWithChecksum(bucketName, key);
        Assertions.assertEquals(encodedChecksum, objectWithChecksum.checksumCRC32());
    }

    @Test
    @Tag("IntegrationTest")
    void putObjectWithPrecalculatedChecksumTest() {
        String fullFilePath = BasicOpsWithChecksums.getFullFilePath("/multipartUploadFiles/java_dev_guide_v2.pdf");
        basicOpsWithChecksums.putObjectWithPrecalculatedChecksum(bucketName, key, fullFilePath);

        GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).checksumMode(ChecksumMode.ENABLED)).response();
        try {
            Assertions.assertNotNull(response.checksumSHA256());
            Assertions.assertEquals(response.checksumSHA256(), BasicOpsWithChecksums.calculateChecksum(fullFilePath, "SHA-256"));
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    @Tag("IntegrationTest")
    void multipartUploadWithChecksumTmTest() {
        try {
            basicOpsWithChecksums.multipartUploadWithChecksumTm(bucketName, key,
                    BasicOpsWithChecksums.getFullFilePath("/multipartUploadFiles/java_dev_guide_v2.pdf"));
            GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).checksumMode(ChecksumMode.ENABLED)).response();
            Assertions.assertNotNull(response.checksumSHA1());
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    @Tag("IntegrationTest")
    void multipartUploadWithChecksumS3ClientOne() {
        try {
            basicOpsWithChecksums.multipartUploadWithChecksumS3Client(bucketName, key,
                    BasicOpsWithChecksums.getFullFilePath("/multipartUploadFiles/java_dev_guide_v2.pdf"));
            GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).checksumMode(ChecksumMode.ENABLED)).response();
            Assertions.assertNotNull(response.checksumCRC32());
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        }
    }

    private String calculateChecksumForString(String input, Algorithm checksumAlgorithm) {
        SdkChecksum checksum = SdkChecksum.forAlgorithm(checksumAlgorithm);
        checksum.update(input.getBytes(StandardCharsets.UTF_8));
        return BinaryUtils.toBase64(checksum.getChecksumBytes());
    }
}
