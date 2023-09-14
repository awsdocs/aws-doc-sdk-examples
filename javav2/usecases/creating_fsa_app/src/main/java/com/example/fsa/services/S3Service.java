/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.Upload;
import java.io.IOException;
import java.io.InputStream;

public class S3Service {

    private static S3AsyncClient s3AsyncClient;

    private static synchronized S3AsyncClient getS3AsyncClient() {
        if (s3AsyncClient == null) {
            s3AsyncClient = S3AsyncClient.crtBuilder()
                .region(Region.US_EAST_1)
                .build();
        }
        return s3AsyncClient;
    }

    // Put the audio file into the Amazon S3 bucket.
    public String putAudio(InputStream is, String bucketName, String key) throws S3Exception, IOException {
        try {
            S3TransferManager transferManager = S3TransferManager.builder()
                .s3Client(getS3AsyncClient())
                .build();

            BlockingInputStreamAsyncRequestBody body = AsyncRequestBody.forBlockingInputStream(null); // 'null' indicates a stream will be provided later.
            Upload upload = transferManager.upload(builder -> builder
                .requestBody(body)
                .putObjectRequest(req -> req.bucket(bucketName).key(key))
                .build());

            body.writeInputStream(is);
            upload.completionFuture().join();
            return key;

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
}
