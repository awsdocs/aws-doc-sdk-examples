package com.example.s3.presignurl;

import com.example.s3.GeneratePresignedUrlAndPutFileWithMetadata;
import com.example.s3.GeneratePresignedUrlAndUploadObject;
import com.example.s3.util.PresignUrlUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

class GeneratePresignedUrlAndPutTests {
    private static final String BUCKET_NAME = "b-" + UUID.randomUUID();
    private static final String KEY_NAME = "k-" + UUID.randomUUID();
    private static final S3Client s3Client = S3Client.create();

    @BeforeAll
    static void beforeAll() {
        PresignUrlUtils.createBucket(BUCKET_NAME, s3Client);
    }

    @AfterAll
    static void afterAll() {
        PresignUrlUtils.deleteObject(BUCKET_NAME, KEY_NAME, s3Client);
        PresignUrlUtils.deleteBucket(BUCKET_NAME, s3Client);
    }

    @Test
    @Tag("IntegrationTest")
    void testCreatePresignedUrlForPutString() {
        GeneratePresignedUrlAndUploadObject presign = new GeneratePresignedUrlAndUploadObject();
        final URL presignedUrl = presign.createSignedUrlForStringPut(BUCKET_NAME, KEY_NAME);

        presign.useHttpUrlConnectionToPutString(presignedUrl);
        Assertions.assertTrue(objectExists());

        presign.useHttpClientToPutString(presignedUrl);
        Assertions.assertTrue(objectExists());
    }

    @Test
    @Tag("IntegrationTest")
    void testCreatePresignedUrlForPutObject() {
        GeneratePresignedUrlAndPutFileWithMetadata presign = new GeneratePresignedUrlAndPutFileWithMetadata();

        File pdfFile = GeneratePresignedUrlAndPutFileWithMetadata.getFileForForClasspathResource("multipartUploadFiles/s3-userguide.pdf");
        String contentType = "application/pdf";
        Map<String, String> metadata = Map.of("meta1", "value1");

        final URL presignedUrl = presign.createPresignedUrl(BUCKET_NAME, KEY_NAME, contentType, metadata);

        presign.useHttpUrlConnectionToPut(presignedUrl, pdfFile, contentType, metadata);
        Assertions.assertTrue(objectHasMetadata(metadata));

        presign.useHttpClientToPut(presignedUrl, pdfFile, contentType, metadata);
        Assertions.assertTrue(objectHasMetadata(metadata));

    }

    private static Boolean objectExists() {
        GetObjectResponse response = s3Client.getObject(b -> b.bucket(BUCKET_NAME).key(KEY_NAME)).response();
        return response.contentLength() > 1L;
    }

    private static Boolean objectHasMetadata(Map<String, String> metadata) {
        GetObjectResponse response = s3Client.getObject(b -> b.bucket(BUCKET_NAME).key(KEY_NAME)).response();
        // The SDK strips off the leading "x-amz-meta-" from the metadata key.
        return response.metadata().get("meta1").equals(metadata.get("meta1"));
    }
}