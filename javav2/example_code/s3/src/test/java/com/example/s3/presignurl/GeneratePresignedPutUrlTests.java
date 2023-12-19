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

class GeneratePresignedPutUrlTests {
    private static final String BUCKET_NAME = "b-" + UUID.randomUUID();
    private static final String KEY_NAME = "k-" + UUID.randomUUID();
    private static final S3Client s3Client = S3Client.create();
    private static final Map<String, String> METADATA = Map.of("meta1", "value1");
    private static final File PDF_FILE = GeneratePresignedUrlAndPutFileWithMetadata.getFileForForClasspathResource("multipartUploadFiles/s3-userguide.pdf");


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
    void testCreatePresignedUrlForPut() {
        GeneratePresignedUrlAndPutFileWithMetadata presignInstanceUnderTest = new GeneratePresignedUrlAndPutFileWithMetadata();

        final String presignedUrlString = presignInstanceUnderTest.createPresignedUrl(BUCKET_NAME, KEY_NAME, METADATA);
        Assertions.assertTrue(presignedUrlString.contains("meta1"));
    }

    @Test
    @Tag("IntegrationTest")
    void testUsingHttpUrlConnection() {
        GeneratePresignedUrlAndPutFileWithMetadata presignInstanceUnderTest = new GeneratePresignedUrlAndPutFileWithMetadata();
        final String presignedUrlString = presignInstanceUnderTest.createPresignedUrl(BUCKET_NAME, KEY_NAME, METADATA);

        presignInstanceUnderTest.useHttpUrlConnectionToPut(presignedUrlString, PDF_FILE, METADATA);
        Assertions.assertTrue(objectHasMetadata());

        PresignUrlUtils.deleteObject(BUCKET_NAME, KEY_NAME, s3Client);
    }

    @Test
    @Tag("IntegrationTest")
    void testUsingSdkHttpClient() {
        GeneratePresignedUrlAndPutFileWithMetadata presignInstanceUnderTest = new GeneratePresignedUrlAndPutFileWithMetadata();
        final String presignedUrlString = presignInstanceUnderTest.createPresignedUrl(BUCKET_NAME, KEY_NAME, METADATA);

        presignInstanceUnderTest.useSdkHttpClientToPut(presignedUrlString, PDF_FILE, METADATA);
        Assertions.assertTrue(objectHasMetadata());

        PresignUrlUtils.deleteObject(BUCKET_NAME, KEY_NAME, s3Client);
    }

    @Test
    @Tag("IntegrationTest")
    void testUsingJdkHttpClient() {
        GeneratePresignedUrlAndPutFileWithMetadata presignInstanceUnderTest = new GeneratePresignedUrlAndPutFileWithMetadata();
        final String presignedUrlString = presignInstanceUnderTest.createPresignedUrl(BUCKET_NAME, KEY_NAME, METADATA);

        presignInstanceUnderTest.useHttpClientToPut(presignedUrlString, PDF_FILE, METADATA);
        Assertions.assertTrue(objectHasMetadata());

        PresignUrlUtils.deleteObject(BUCKET_NAME, KEY_NAME, s3Client);
    }

    private static Boolean objectExists() {
        GetObjectResponse response = s3Client.getObject(b -> b.bucket(BUCKET_NAME).key(KEY_NAME)).response();
        return response.contentLength() > 1L;
    }

    private static Boolean objectHasMetadata() {
        GetObjectResponse response = s3Client.getObject(b -> b.bucket(BUCKET_NAME).key(KEY_NAME)).response();
        // The SDK strips off the leading "x-amz-meta-" from the metadata key.
        return response.metadata().get("meta1").equals(GeneratePresignedPutUrlTests.METADATA.get("meta1"));
    }
}