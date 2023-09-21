package com.example.s3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

class PerformMultiPartUploadTests {
    private final S3Client s3Client = PerformMultiPartUpload.s3Client;
    private final String bucketName = PerformMultiPartUpload.bucketName;
    private final String key = PerformMultiPartUpload.key;
    private PerformMultiPartUpload performMultiPartUpload;

    @BeforeEach
    void setUp() {
        performMultiPartUpload = new PerformMultiPartUpload();
    }

    @Test
    @Tag("IntegrationTest")
    void multipartUploadWithTransferManagerTest() {
        PerformMultiPartUpload.createBucket();
        try {
            performMultiPartUpload.multipartUploadWithTransferManager(PerformMultiPartUpload.getFullFilePath("/multipartUploadFiles/s3-userguide.pdf"));
            GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).partNumber(1)).response();
            Assertions.assertTrue(response.partsCount() > 1);
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        } finally {
            PerformMultiPartUpload.deleteResources();
        }
    }

    @Test
    @Tag("IntegrationTest")
    void multipartUploadWithS3ClientTest(){
        PerformMultiPartUpload.createBucket();
        try {
            performMultiPartUpload.multipartUploadWithS3Client(PerformMultiPartUpload.getFullFilePath("/multipartUploadFiles/s3-userguide.pdf"));
            GetObjectResponse response = s3Client.getObject(b -> b.bucket(bucketName).key(key).partNumber(1)).response();
            Assertions.assertTrue(response.partsCount() > 1);
        } catch (SdkException e) {
            System.err.println(e.getMessage());
        } finally {
            PerformMultiPartUpload.deleteResources();
        }
    }
}