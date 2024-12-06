// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.directorybucket;

// snippet-start:[s3directorybuckets.java2.directory_bucket_upload_part_copy.import]

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.example.s3.util.S3DirectoryBucketUtils.abortDirectoryBucketMultipartUploads;
import static com.example.s3.util.S3DirectoryBucketUtils.completeDirectoryBucketMultipartUpload;
import static com.example.s3.util.S3DirectoryBucketUtils.createDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.createDirectoryBucketMultipartUpload;
import static com.example.s3.util.S3DirectoryBucketUtils.createS3Client;
import static com.example.s3.util.S3DirectoryBucketUtils.deleteAllObjectsInDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.deleteDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.getFilePath;
import static com.example.s3.util.S3DirectoryBucketUtils.multipartUploadForDirectoryBucket;
// snippet-end:[s3directorybuckets.java2.directory_bucket_upload_part_copy.import]

/**
 * Before running this example:
 * <p>
 * The SDK must be able to authenticate AWS requests on your behalf. If you have
 * not configured
 * authentication for SDKs and tools, see
 * https://docs.aws.amazon.com/sdkref/latest/guide/access.html in the AWS SDKs
 * and Tools Reference Guide.
 * <p>
 * You must have a runtime environment configured with the Java SDK.
 * See
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html in
 * the Developer Guide if this is not set up.
 * <p>
 * To use S3 directory buckets, configure a gateway VPC endpoint. This is the
 * recommended method to enable directory bucket traffic without
 * requiring an internet gateway or NAT device. For more information on
 * configuring VPC gateway endpoints, visit
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/s3-express-networking.html#s3-express-networking-vpc-gateway.
 * <p>
 * Directory buckets are available in specific AWS Regions and Zones. For
 * details on Regions and Zones supporting directory buckets, see
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/s3-express-networking.html#s3-express-endpoints.
 */

public class UploadPartCopyForDirectoryBucket {
    private static final Logger logger = LoggerFactory.getLogger(UploadPartCopyForDirectoryBucket.class);

    // snippet-start:[s3directorybuckets.java2.directory_bucket_upload_part_copy.main]
    /**
     * Creates copy parts based on source object size and copies over individual
     * parts.
     *
     * @param s3Client          The S3 client used to interact with S3
     * @param sourceBucket      The name of the source bucket
     * @param sourceKey         The key (name) of the source object
     * @param destinationBucket The name of the destination bucket
     * @param destinationKey    The key (name) of the destination object
     * @param uploadId          The upload ID used to track the multipart upload
     * @return A list of completed parts
     */
    public static List<CompletedPart> multipartUploadCopyForDirectoryBucket(S3Client s3Client, String sourceBucket,
            String sourceKey, String destinationBucket, String destinationKey, String uploadId) {
        // Get the object size to track the end of the copy operation
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(sourceBucket)
                .key(sourceKey)
                .build();
        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
        long objectSize = headObjectResponse.contentLength();

        logger.info("Source Object size: {}", objectSize);

        // Copy the object using 20 MB parts
        long partSize = 20 * 1024 * 1024; // 20 MB
        long bytePosition = 0;
        int partNum = 1;
        List<CompletedPart> uploadedParts = new ArrayList<>();

        while (bytePosition < objectSize) {
            long lastByte = Math.min(bytePosition + partSize - 1, objectSize - 1);
            logger.info("Part Number: {}, Byte Position: {}, Last Byte: {}", partNum, bytePosition, lastByte);

            try {
                UploadPartCopyRequest uploadPartCopyRequest = UploadPartCopyRequest.builder()
                        .sourceBucket(sourceBucket)
                        .sourceKey(sourceKey)
                        .destinationBucket(destinationBucket)
                        .destinationKey(destinationKey)
                        .uploadId(uploadId)
                        .copySourceRange("bytes=" + bytePosition + "-" + lastByte)
                        .partNumber(partNum)
                        .build();
                UploadPartCopyResponse uploadPartCopyResponse = s3Client.uploadPartCopy(uploadPartCopyRequest);

                CompletedPart part = CompletedPart.builder()
                        .partNumber(partNum)
                        .eTag(uploadPartCopyResponse.copyPartResult().eTag())
                        .build();
                uploadedParts.add(part);

                bytePosition += partSize;
                partNum++;
            } catch (S3Exception e) {
                logger.error("Failed to copy part number {}: {} - Error code: {}", partNum,
                        e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
                throw e;
            }
        }

        return uploadedParts;
    }
    // snippet-end:[s3directorybuckets.java2.directory_bucket_upload_part_copy.main]

    // Main method for testing
    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
        S3Client s3Client = createS3Client(region);
        String zone = "usw2-az1";
        String sourceDirectoryBucket = "test-source-bucket-" + System.currentTimeMillis() + "--" + zone + "--x-s3";
        String targetDirectoryBucket = "test-destination-bucket-" + System.currentTimeMillis() + "--" + zone + "--x-s3";
        String sourceObjectKey = "source-large-object"; // your-source-object-key
        String destinationObjectKey = "dest-large-object"; // your-destination-object-key
        Path filePath = getFilePath("directoryBucket/sample-large-object.jpg"); // path to your file.
        String uploadIdSource;
        String uploadIdDest;

        try {
            // Create the source and target directory buckets
            createDirectoryBucket(s3Client, sourceDirectoryBucket, zone);
            createDirectoryBucket(s3Client, targetDirectoryBucket, zone);
            // Create a multipart upload to upload the large object to the source directory
            // bucket
            uploadIdSource = createDirectoryBucketMultipartUpload(s3Client, sourceDirectoryBucket, sourceObjectKey);
            // Perform multipart upload for the directory bucket
            List<CompletedPart> uploadedPartsSource = multipartUploadForDirectoryBucket(s3Client, sourceDirectoryBucket,
                    sourceObjectKey, uploadIdSource, filePath);
            // Complete Multipart Uploads
            completeDirectoryBucketMultipartUpload(s3Client, sourceDirectoryBucket, sourceObjectKey, uploadIdSource,
                    uploadedPartsSource);

            // Create a multipart upload to upload the large object to the destination
            // directory bucket
            uploadIdDest = createDirectoryBucketMultipartUpload(s3Client, targetDirectoryBucket, destinationObjectKey);
            // Perform multipart upload copy for the directory bucket
            List<CompletedPart> uploadedPartsDestination = multipartUploadCopyForDirectoryBucket(s3Client,
                    sourceDirectoryBucket, sourceObjectKey, targetDirectoryBucket, destinationObjectKey, uploadIdDest);
            // Complete the multipart upload
            completeDirectoryBucketMultipartUpload(s3Client, targetDirectoryBucket, destinationObjectKey, uploadIdDest,
                    uploadedPartsDestination);

            logger.info("Multipart upload copy completed for source object: {} to the object copy: {}", sourceObjectKey,
                    destinationObjectKey);
        } catch (S3Exception e) {
            logger.error("Failed to complete multipart copy: {} - Error code: {}", e.awsErrorDetails().errorMessage(),
                    e.awsErrorDetails().errorCode(), e);
        } catch (IOException e) {
            logger.error("An I/O error occurred: {}", e.getMessage(), e);
        } finally {
            // Combined try-catch for cleanup operations
            try {
                logger.info("Aborting Multipart Uploads in bucket: {}", sourceDirectoryBucket);
                abortDirectoryBucketMultipartUploads(s3Client, sourceDirectoryBucket);
                logger.info("Aborting Multipart Uploads in bucket: {}", targetDirectoryBucket);
                abortDirectoryBucketMultipartUploads(s3Client, targetDirectoryBucket);

                logger.info("Deleting the objects in bucket: {}", sourceDirectoryBucket);
                deleteAllObjectsInDirectoryBucket(s3Client, sourceDirectoryBucket);
                logger.info("Deleting the objects in bucket: {}", targetDirectoryBucket);
                deleteAllObjectsInDirectoryBucket(s3Client, targetDirectoryBucket);

                deleteDirectoryBucket(s3Client, sourceDirectoryBucket);
                deleteDirectoryBucket(s3Client, targetDirectoryBucket);
            } catch (S3Exception e) {
                logger.error("Failed to clean up S3 resources: {} - Error code: {}", e.awsErrorDetails().errorMessage(),
                        e.awsErrorDetails().errorCode(), e);
            } catch (RuntimeException e) {
                logger.error("Failed to clean up resources due to unexpected error: {}", e.getMessage(), e);
            } finally {
                s3Client.close();
            }
        }
    }
}
