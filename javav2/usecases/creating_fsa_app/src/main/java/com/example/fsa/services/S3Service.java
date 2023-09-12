/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class S3Service {

    private static S3AsyncClient s3AsyncClient;

    private static synchronized S3AsyncClient getS3AsyncClient() {
        if (s3AsyncClient == null) {
            s3AsyncClient = S3AsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return s3AsyncClient;
    }

    // Put the audio file into the Amazon S3 bucket.
    public String putAudio(InputStream is, String bucket, String key) throws S3Exception, IOException {
        try {
            S3TransferManager transferManager = S3TransferManager.builder()
                .s3Client(getS3AsyncClient())
                .build();

            byte[] bytes = inputStreamToBytes(is);
            long contentLength = bytes.length;
            UploadRequest uploadRequest = UploadRequest.builder()
                .requestBody(AsyncRequestBody.fromBytes(bytes))
                .putObjectRequest(PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("audio/mp3")
                    .contentLength(contentLength)
                    .build())
                .build();

            Upload upload = transferManager.upload(uploadRequest);
            // Wait for the transfer to complete
            CompletableFuture<?> future = upload.completionFuture();
            future.join();
            return key;

        } catch (IOException | S3Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private static byte[] inputStreamToBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }
}
