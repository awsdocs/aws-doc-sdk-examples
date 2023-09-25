package com.example.s3;

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

class BasicOpsWithChecksumsTests {
    private final S3Client s3Client = BasicOpsWithChecksums.s3Client;
    private final String bucketName = BasicOpsWithChecksums.bucketName;
    private final String key = BasicOpsWithChecksums.key;
    private BasicOpsWithChecksums basicOpsWithChecksums;

    @BeforeEach
    void setUp() {
        basicOpsWithChecksums = new BasicOpsWithChecksums();
    }

    @Test
    @Tag("IntegrationTest")
    void putObjectWithChecksumTest() {
        BasicOpsWithChecksums.createBucket();
        // Method to test.
        basicOpsWithChecksums.putObjectWithChecksum();

        GetObjectAttributesResponse objectAttributes = s3Client.getObjectAttributes(b -> b
                .bucket(bucketName)
                .key(key)
                .objectAttributes(ObjectAttributes.CHECKSUM));
        Assertions.assertNotNull(objectAttributes.checksum());

        BasicOpsWithChecksums.deleteResources();
    }

    @Test
    @Tag("IntegrationTest")
    void getObjectWithChecksumTest() {

        String stringObjToUpload = "This is a test";
        String encodedChecksum = calculateChecksumForString(stringObjToUpload, Algorithm.CRC32);


        BasicOpsWithChecksums.createBucket();
        // Method to test.
        basicOpsWithChecksums.putObjectWithChecksum();


        GetObjectResponse objectWithChecksum = basicOpsWithChecksums.getObjectWithChecksum();
        Assertions.assertEquals(encodedChecksum, objectWithChecksum.checksumCRC32());
        BasicOpsWithChecksums.deleteResources();

    }

    @Test
    @Tag("IntegrationTest")
    void putObjectWithPrecalculatedChecksumTest() {
        BasicOpsWithChecksums.createBucket();
        String fullFilePath = BasicOpsWithChecksums.getFullFilePath("/multipartUploadFiles/java_dev_guide_v2.pdf");
        // Method to test.
        basicOpsWithChecksums.putObjectWithPrecalculatedChecksum(fullFilePath);

        GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).checksumMode(ChecksumMode.ENABLED)).response();
        try {
            Assertions.assertNotNull(response.checksumSHA256());
            Assertions.assertEquals(response.checksumSHA256(), BasicOpsWithChecksums.calculateChecksum(fullFilePath, "SHA-256"));
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        } finally {
            BasicOpsWithChecksums.deleteResources();
        }
    }

    @Test
    @Tag("IntegrationTest")
    void multipartUploadWithChecksumTmTest() {
        BasicOpsWithChecksums.createBucket();
        try {
            basicOpsWithChecksums.multipartUploadWithChecksumTm(BasicOpsWithChecksums.getFullFilePath("/multipartUploadFiles/java_dev_guide_v2.pdf"));
            GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).checksumMode(ChecksumMode.ENABLED)).response();
            Assertions.assertNotNull(response.checksumSHA1());
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        } finally {
            BasicOpsWithChecksums.deleteResources();
        }
    }

    @Test
    @Tag("IntegrationTest")
    void multipartUploadWithChecksumS3ClientOne(){
        BasicOpsWithChecksums.createBucket();
        try {
            basicOpsWithChecksums.multipartUploadWithChecksumS3Client(BasicOpsWithChecksums.getFullFilePath("/multipartUploadFiles/java_dev_guide_v2.pdf"));
            GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).checksumMode(ChecksumMode.ENABLED)).response();
            Assertions.assertNotNull(response.checksumCRC32());
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        } finally {
            BasicOpsWithChecksums.deleteResources();
        }
    }

    private String calculateChecksumForString(String input, Algorithm checksumAlgorithm) {
        SdkChecksum checksum = SdkChecksum.forAlgorithm(checksumAlgorithm);
        checksum.update(input.getBytes(StandardCharsets.UTF_8));
        return BinaryUtils.toBase64(checksum.getChecksumBytes());
    }
}