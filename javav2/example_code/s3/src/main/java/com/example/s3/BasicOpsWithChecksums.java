/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[s3.java2.basicOpsWithChecksums.import]

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.ChecksumMode;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
// snippet-end:[s3.java2.basicOpsWithChecksums.import]

// snippet-start:[s3.java2.basicOpsWithChecksums.full]
public class BasicOpsWithChecksums {
    static final S3Client s3Client = S3Client.create();
    static final String bucketName = "x-" + UUID.randomUUID();
    static final String key = UUID.randomUUID().toString();
    private static final Logger logger = LoggerFactory.getLogger(BasicOpsWithChecksums.class);

    public static void main(String[] args) {
        BasicOpsWithChecksums basicOpsWithChecksums = new BasicOpsWithChecksums();
        basicOpsWithChecksums.doPutBucket();
        basicOpsWithChecksums.doGetBucket();
        basicOpsWithChecksums.doPutObjectWithPrecalculatedChecksum();
        basicOpsWithChecksums.doMultipartUploadWithChecksumTm();
        basicOpsWithChecksums.doMultipartUploadWithChecksumS3Client();
    }

    static String getFullFilePath(String filePath) {
        URL uploadDirectoryURL = BasicOpsWithChecksums.class.getResource(filePath);
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

    static String calculateChecksum(String filePath, String algorithm) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filePath), md)) {
            byte[] buffer = new byte[8192];
            int numBytesRead = 0;
            while (numBytesRead != -1)
                numBytesRead = dis.read(buffer);
            return Base64.getEncoder().encodeToString(md.digest());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // snippet-start:[s3.java2.basicOpsWithChecksums.putObject]
    public void putObjectWithChecksum() {
        s3Client.putObject(b -> b
                        .bucket(bucketName)
                        .key(key)
                        .checksumAlgorithm(ChecksumAlgorithm.CRC32),
                RequestBody.fromString("This is a test")
        );
    }
    // snippet-end:[s3.java2.basicOpsWithChecksums.putObject]

    // snippet-start:[s3.java2.basicOpsWithChecksums.getObject]
    public GetObjectResponse getObjectWithChecksum() {
        return s3Client.getObject(b -> b
                        .bucket(bucketName)
                        .key(key)
                        .checksumMode(ChecksumMode.ENABLED))
                .response();
    }
    // snippet-end:[s3.java2.basicOpsWithChecksums.getObject]

    // snippet-start:[s3.java2.basicOpsWithChecksums.putObjectPreCalc]
    public void putObjectWithPrecalculatedChecksum(String filePath) {
        String checksum = calculateChecksum(filePath, "SHA-256");


        s3Client.putObject((b -> b
                        .bucket(bucketName)
                        .key(key)
                        .checksumSHA256(checksum)),
                RequestBody.fromFile(Paths.get(filePath)));
    }
    // snippet-end:[s3.java2.basicOpsWithChecksums.putObjectPreCalc]

    // snippet-start:[s3.java2.basicOpsWithChecksums.multiPartTm]
    public void multipartUploadWithChecksumTm(String filePath) {
        S3TransferManager transferManager = S3TransferManager.create();
        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                .putObjectRequest(b -> b
                        .bucket(bucketName)
                        .key(key)
                        .checksumAlgorithm(ChecksumAlgorithm.SHA1))
                .source(Paths.get(filePath))
                .build();
        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
        fileUpload.completionFuture().join();
        transferManager.close();
    }
    // snippet-end:[s3.java2.basicOpsWithChecksums.multiPartTm]

    // snippet-start:[s3.java2.basicOpsWithChecksums.multiPartS3Client]
    public void multipartUploadWithChecksumS3Client(String filePath) {
        ChecksumAlgorithm algorithm = ChecksumAlgorithm.CRC32;

        // Initiate the multipart upload.
        CreateMultipartUploadResponse createMultipartUploadResponse =
                s3Client.createMultipartUpload(b -> b
                        .bucket(bucketName)
                        .key(key)
                        .checksumAlgorithm(algorithm)); // Checksum specified on initiation.
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
                        .checksumAlgorithm(algorithm)  //Checksum specified on each part.
                        .partNumber(partNumber)
                        .build();

                UploadPartResponse partResponse = s3Client.uploadPart(
                        uploadPartRequest,
                        RequestBody.fromByteBuffer(bb));

                CompletedPart part = CompletedPart.builder()
                        .partNumber(partNumber)
                        .checksumCRC32(partResponse.checksumCRC32()) // Provide the calculated checksum.
                        .eTag(partResponse.eTag())
                        .build();
                completedParts.add(part);

                bb.clear();
                position += read;
                partNumber++;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // Complete the multipart upload.
        s3Client.completeMultipartUpload(b -> b
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build()));
    }
    // snippet-end:[s3.java2.basicOpsWithChecksums.multiPartS3Client]

    private void doPutBucket() {
        createBucket();
        putObjectWithChecksum();
        deleteResources();
    }

    private void doGetBucket() {
        createBucket();
        putObjectWithChecksum();
        getObjectWithChecksum();
        deleteResources();
    }

    private void doPutObjectWithPrecalculatedChecksum() {
        createBucket();
        try {
            putObjectWithPrecalculatedChecksum(getFullFilePath("/multipartUploadFiles/java_dev_guide_v2.pdf"));
        } catch (SdkException e) {
            logger.error(e.getMessage());
        } finally {
            deleteResources();
        }
    }

    private void doMultipartUploadWithChecksumS3Client() {
        createBucket();
        try {
            multipartUploadWithChecksumS3Client(getFullFilePath("/multipartUploadFiles/java_dev_guide_v2.pdf"));
        } catch (SdkException e) {
            logger.error(e.getMessage());
        } finally {
            deleteResources();
        }
    }

    private void doMultipartUploadWithChecksumTm() {
        createBucket();
        try {
            multipartUploadWithChecksumTm(getFullFilePath("/multipartUploadFiles/java_dev_guide_v2.pdf"));
        } catch (SdkException e) {
            logger.error(e.getMessage());
        } finally {
            deleteResources();
        }
    }
}
// snippet-end:[s3.java2.basicOpsWithChecksums.full]
