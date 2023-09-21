/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[s3.java2.performMultiPartUpload.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
// snippet-end:[s3.java2.performMultiPartUpload.import]

// snippet-start:[s3.java2.performMultiPartUpload.full]
public class PerformMultiPartUpload {
    static final S3Client s3Client = S3Client.create();
    static final String bucketName = "x-" + UUID.randomUUID();
    static final String key = UUID.randomUUID().toString();
    private static final Logger logger = LoggerFactory.getLogger(PerformMultiPartUpload.class);

    public static void main(String[] args) {
        PerformMultiPartUpload performMultiPartUpload = new PerformMultiPartUpload();
        performMultiPartUpload.doMultipartUploadWithTransferManager();
        performMultiPartUpload.doMultipartUploadWithS3Client();
    }

    static String getFullFilePath(String filePath) {
        URL uploadDirectoryURL = PerformMultiPartUpload.class.getResource(filePath);
        String fullFilePath;
        try {
            fullFilePath = Objects.requireNonNull(uploadDirectoryURL).toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return fullFilePath;
    }

    static void createBucket() {
        s3Client.createBucket(b -> b.bucket(bucketName));
        try (S3Waiter s3Waiter = s3Client.waiter()) {
            s3Waiter.waitUntilBucketExists(b -> b.bucket(bucketName));
        }
    }

    static void deleteResources() {
        s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
        s3Client.deleteBucket(b -> b.bucket(bucketName));
    }

    // snippet-start:[s3.java2.performMultiPartUpload.transferManager]
    public void multipartUploadWithTransferManager(String filePath) {
        S3TransferManager transferManager = S3TransferManager.create();
        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                .putObjectRequest(b -> b
                        .bucket(bucketName)
                        .key(key))
                .source(Paths.get(filePath))
                .build();
        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
        fileUpload.completionFuture().join();
        transferManager.close();
    }
    // snippet-end:[s3.java2.performMultiPartUpload.transferManager]

    // snippet-start:[s3.java2.performMultiPartUpload.s3Client]
    public void multipartUploadWithS3Client(String filePath) {

        // Initiate the multipart upload.
        CreateMultipartUploadResponse createMultipartUploadResponse =
                s3Client.createMultipartUpload(b -> b
                        .bucket(bucketName)
                        .key(key));
        String uploadId = createMultipartUploadResponse.uploadId();

        // Upload the parts of the file.
        int partNumber = 1;
        List<CompletedPart> completedParts = new ArrayList<>();
        ByteBuffer bb = ByteBuffer.allocate(1024 * 1024 * 5); // 5 MB byte buffer

        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            long fileSize = file.length();
            int position = 0;
            while (position < fileSize) {
                file.seek(position);
                int read = file.getChannel().read(bb);

                bb.flip(); // Swap position and limit before reading from the buffer.
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build();

                UploadPartResponse partResponse = s3Client.uploadPart(
                        uploadPartRequest,
                        RequestBody.fromByteBuffer(bb));

                CompletedPart part = CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(partResponse.eTag())
                        .build();
                completedParts.add(part);

                bb.clear();
                position += read;
                partNumber++;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        // Complete the multipart upload.
        s3Client.completeMultipartUpload(b -> b
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build()));
    }
    // snippet-end:[s3.java2.performMultiPartUpload.s3Client]

    private void doMultipartUploadWithS3Client() {
        createBucket();
        try {
            multipartUploadWithS3Client(getFullFilePath("/multipartUploadFiles/s3-userguide.pdf"));
        } catch (SdkException e) {
            logger.error(e.getMessage());
        } finally {
            deleteResources();
        }
    }

    private void doMultipartUploadWithTransferManager() {
        createBucket();
        try {
            multipartUploadWithTransferManager(getFullFilePath("/multipartUploadFiles/s3-userguide.pdf"));
        } catch (SdkException e) {
            logger.error(e.getMessage());
        } finally {
            deleteResources();
        }
    }
}
// snippet-end:[s3.java2.performMultiPartUpload.full]
