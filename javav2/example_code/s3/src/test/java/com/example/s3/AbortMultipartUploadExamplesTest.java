// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationResponse;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsResponse;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AbortMultipartUploadExamplesTest {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AbortMultipartUploadExamplesTest.class);
    private String bucketName;
    private String key;

    @BeforeEach
    void setUp() {
        bucketName = "test-bucket-" + UUID.randomUUID();
        key = UUID.randomUUID().toString();
        AbortMultipartUploadExamples.createBucket(bucketName);
    }

    @AfterEach
    void tearDown() {
        AbortMultipartUploadExamples.deleteResources(bucketName, key);
    }

    @Test
    @Tag("IntegrationTest")
    void testNoUploadsAreInProgressAfterAbort() {
        AbortMultipartUploadExamples.initiateAndInterruptMultiPartUpload(bucketName, key, "uploadThread");

        AbortMultipartUploadExamples.abortIncompleteMultipartUploadsFromList(bucketName);
        ListMultipartUploadsResponse listMultipartUploadsResponse = AbortMultipartUploadExamples.s3Client
                .listMultipartUploads(b -> b.bucket(bucketName));
        logger.info("Incomplete uploads: {}", listMultipartUploadsResponse.uploads().size());
        assertTrue(listMultipartUploadsResponse.uploads().isEmpty());
    }

    @Test
    @Tag("IntegrationTest")
    void testNoIncompleteUploadsUsingUploadIdToAbort() {
        AbortMultipartUploadExamples.abortMultipartUploadUsingUploadId(bucketName, key);
        try {
            Thread.sleep(3000); // Allow time for abort to propagate and upload thread to terminate.
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // After abort, also clean up any remaining uploads.
        AbortMultipartUploadExamples.abortIncompleteMultipartUploadsFromList(bucketName);
        ListMultipartUploadsResponse listMultipartUploadsResponse = AbortMultipartUploadExamples.s3Client
                .listMultipartUploads(b -> b.bucket(bucketName));
        logger.info("Incomplete uploads: {}", listMultipartUploadsResponse.uploads().size());
        assertTrue(listMultipartUploadsResponse.uploads().isEmpty());
    }

    @Test
    @Tag("IntegrationTest")
    void testOneIncompleteUploadExistsWhenUsingOlderThanInstantAbort() {
        Instant secondUploadInstant = AbortMultipartUploadExamples.initiateAndInterruptTwoUploads(bucketName, key);
        AbortMultipartUploadExamples.abortIncompleteMultipartUploadsOlderThan(bucketName, secondUploadInstant);
        ListMultipartUploadsResponse listMultipartUploadsResponse = AbortMultipartUploadExamples.s3Client
                .listMultipartUploads(b -> b.bucket(bucketName));
        logger.info("Incomplete uploads: {}", listMultipartUploadsResponse.uploads().size());
        assertFalse(listMultipartUploadsResponse.uploads().isEmpty());
    }

    @Test
    @Tag("IntegrationTest")
    void testAbortMultipartUploadsUsingLifecycleConfigHasRule() {
        AbortMultipartUploadExamples.abortMultipartUploadsUsingLifecycleConfig(bucketName);
        GetBucketLifecycleConfigurationResponse response = AbortMultipartUploadExamples.s3Client
                .getBucketLifecycleConfiguration(b -> b.bucket(bucketName));
        assertEquals(7, (int) response.rules().get(0).abortIncompleteMultipartUpload().daysAfterInitiation());
    }
}
