// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.s3;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DoesObjectExistTest {

    private final S3Client s3Client = mock(S3Client.class);
    private final DoesObjectExist doesObjectExist = new DoesObjectExist();

    @Test
    void doesObjectExist_objectExists_returnsTrue() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        assertTrue(doesObjectExist.doesObjectExist("bucket", "key", s3Client));
    }

    @Test
    void doesObjectExist_objectDoesNotExist_returnsFalse() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().build());

        assertFalse(doesObjectExist.doesObjectExist("bucket", "key", s3Client));
    }

    @Test
    void doesObjectExist_accessDenied_throwsS3Exception() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow((S3Exception) S3Exception.builder().statusCode(403).message("Forbidden").build());

        assertThrows(S3Exception.class, () ->
                doesObjectExist.doesObjectExist("bucket", "key", s3Client));
    }
}
