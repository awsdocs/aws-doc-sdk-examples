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

import static org.junit.jupiter.api.Assertions.*;

class AbortMultipartUploadExamplesTest {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AbortMultipartUploadExamplesTest.class);

    @BeforeEach
    void setUp() {
        AbortMultipartUploadExamples.createBucket();
    }
    @AfterEach
    void tearDown() {
        AbortMultipartUploadExamples.deleteResources();
    }

    @Test
    @Tag("IntegrationTest")
    void testNoUploadsAreInProgressAfterAbort() {
        AbortMultipartUploadExamples.initiateAndInterruptMultiPartUpload("uploadThread");

        AbortMultipartUploadExamples.abortIncompleteMultipartUploadsFromList();
        ListMultipartUploadsResponse listMultipartUploadsResponse = AbortMultipartUploadExamples.s3Client
                .listMultipartUploads(b -> b.
                        bucket(AbortMultipartUploadExamples.bucketName));
        logger.info("Incomplete uploads: {}", listMultipartUploadsResponse.uploads().size());
        assertTrue(listMultipartUploadsResponse.uploads().isEmpty());
    }
    @Test
    @Tag("IntegrationTest")
    void testNoIncompleteUploadsUsingUploadIdToAbort() {
        AbortMultipartUploadExamples.abortMultipartUploadUsingUploadId();
        ListMultipartUploadsResponse listMultipartUploadsResponse = AbortMultipartUploadExamples.s3Client
                .listMultipartUploads(b -> b.
                        bucket(AbortMultipartUploadExamples.bucketName));
        logger.info("Incomplete uploads: {}", listMultipartUploadsResponse.uploads().size());
        assertTrue(listMultipartUploadsResponse.uploads().isEmpty());
    }
    @Test
    @Tag("IntegrationTest")
    void testOneIncompleteUploadExistsWhenUsingOlderThanInstantAbort(){
        Instant secondUploadInstant = AbortMultipartUploadExamples.initiateAndInterruptTwoUploads();
        AbortMultipartUploadExamples.abortIncompleteMultipartUploadsOlderThan(secondUploadInstant);
        ListMultipartUploadsResponse listMultipartUploadsResponse = AbortMultipartUploadExamples.s3Client
                .listMultipartUploads(b -> b.
                        bucket(AbortMultipartUploadExamples.bucketName));
        logger.info("Incomplete uploads: {}", listMultipartUploadsResponse.uploads().size());
        assertFalse(listMultipartUploadsResponse.uploads().isEmpty());
    }

    @Test
    @Tag("IntegrationTest")
    void testAbortMultipartUploadsUsingLifecycleConfigHasRule(){
        AbortMultipartUploadExamples.abortMultipartUploadsUsingLifecycleConfig();
        GetBucketLifecycleConfigurationResponse response = AbortMultipartUploadExamples.s3Client.getBucketLifecycleConfiguration(b -> b
                .bucket(AbortMultipartUploadExamples.bucketName));
        assertEquals(7, (int) response.rules().get(0).abortIncompleteMultipartUpload().daysAfterInitiation());
    }
}