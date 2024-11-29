// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.presignurl;

import com.example.s3.GeneratePresignedUrlAndPutFileWithMetadata;
import com.example.s3.GeneratePresignedUrlAndPutFileWithQueryParams;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class GeneratePresignedPutUrlTests {
    private static final String BUCKET_NAME = "b-" + UUID.randomUUID();
    private static final String KEY_NAME = "k-" + UUID.randomUUID();
    private static final S3Client s3Client = S3Client.create();
    private static final String METADATA_KEY = "meta1";
    private static final String METADATA_VALUE = "value1";
    private static final Map<String, String> METADATA = Map.of(METADATA_KEY, METADATA_VALUE);
    private static final File PDF_FILE = GeneratePresignedUrlAndPutFileWithMetadata
            .getFileForForClasspathResource("multipartUploadFiles/s3-userguide.pdf");

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
    void test_create_presigned_url_using_query_params_does_not_add_to_signed_headers() {
        Map<String, String> queryParams = Map.of(
                "x-amz-meta-author", "Bob",
                "x-amz-meta-version", "1.0.0.0",
                "x-amz-acl", "private",
                "x-amz-server-side-encryption", "AES256"
        );

        GeneratePresignedUrlAndPutFileWithQueryParams presign = new GeneratePresignedUrlAndPutFileWithQueryParams();
        String presignedUrl = presign.createPresignedUrl(BUCKET_NAME,  KEY_NAME, queryParams);

        Map<String, String> queryStringMap = parseQueryString(presignedUrl);
        Assertions.assertFalse(queryStringMap.get("X-Amz-SignedHeaders").contains("x-amz-meta-author"));
        Assertions.assertFalse(queryStringMap.get("X-Amz-SignedHeaders").contains("x-amz-server-side-encryption"));
    }

    @Test
    @Tag("IntegrationTest")
    void test_create_presigned_url_using_query_params_works() {
        Map<String, String> queryParams = Map.of(
                "x-amz-meta-author", "Bob",
                "x-amz-meta-version", "1.0.0.0",
                "x-amz-server-side-encryption", "AES256"
        );

        GeneratePresignedUrlAndPutFileWithQueryParams classUnderTest = new GeneratePresignedUrlAndPutFileWithQueryParams();
        String presignedUrl = classUnderTest.createPresignedUrl(BUCKET_NAME, KEY_NAME, queryParams);

        classUnderTest.useSdkHttpClientToPut(presignedUrl, PDF_FILE);

        try (S3Client s3Client = S3Client.create()){
            s3Client.getObject(builder -> {
                builder.bucket(BUCKET_NAME);
                builder.key(KEY_NAME);
                builder.build();
            }, (response, stream) -> {
                stream.abort();
                Assertions.assertEquals("Bob", response.metadata().get("author"));
                Assertions.assertEquals("1.0.0.0", response.metadata().get("version"));
                Assertions.assertEquals("AES256", response.serverSideEncryptionAsString());
                return null;
            });
        }
    }

    @Test
    @Tag("IntegrationTest")
    void testCreatePresignedUrlForPut() {
        GeneratePresignedUrlAndPutFileWithMetadata presignInstanceUnderTest = new GeneratePresignedUrlAndPutFileWithMetadata();

        final String presignedUrlString = presignInstanceUnderTest.createPresignedUrl(BUCKET_NAME, KEY_NAME, METADATA);
        final Map<String, String> queryParamsMap = parseQueryString(presignedUrlString);
        // Assert that the metadata key, but not the value, is in the signed headers.
        Assertions.assertTrue(queryParamsMap.get("X-Amz-SignedHeaders").contains("x-amz-meta-" + METADATA_KEY));
        Assertions.assertFalse(queryParamsMap.get("X-Amz-SignedHeaders").contains("x-amz-meta-" + METADATA_VALUE));
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

    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();

        // Return empty map for null or empty input
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        // Split the query string into key-value pairs
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            // Find the separator between key and value
            int separatorIndex = pair.indexOf("=");

            // Handle key-only parameters (no value)
            if (separatorIndex <= 0) {
                params.put(URLDecoder.decode(pair, StandardCharsets.UTF_8), null);
                continue;
            }

            // Extract and decode the key
            String key = URLDecoder.decode(
                    pair.substring(0, separatorIndex),
                    StandardCharsets.UTF_8
            );

            // Extract and decode the value if it exists
            String value = null;
            if (pair.length() > separatorIndex + 1) {
                value = URLDecoder.decode(
                        pair.substring(separatorIndex + 1),
                        StandardCharsets.UTF_8
                );
            }

            params.put(key, value);
        }

        return params;
    }

}