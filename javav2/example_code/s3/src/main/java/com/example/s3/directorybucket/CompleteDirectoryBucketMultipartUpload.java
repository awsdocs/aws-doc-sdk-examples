// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.s3.directorybucket;

// snippet-start:[s3directorybuckets.java2.completedirectorybucketmultipartupload.import]

import com.example.s3.util.S3DirectoryBucketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.example.s3.util.S3DirectoryBucketUtils.createDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.createDirectoryBucketMultipartUpload;
import static com.example.s3.util.S3DirectoryBucketUtils.createS3Client;
import static com.example.s3.util.S3DirectoryBucketUtils.deleteAllObjectsInDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.deleteDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.getFilePath;
import static com.example.s3.util.S3DirectoryBucketUtils.multipartUploadForDirectoryBucket;
// snippet-end:[s3directorybuckets.java2.completedirectorybucketmultipartupload.import]

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

public class CompleteDirectoryBucketMultipartUpload {
    private static final Logger logger = LoggerFactory.getLogger(CompleteDirectoryBucketMultipartUpload.class);

    // snippet-start:[s3directorybuckets.java2.completedirectorybucketmultipartupload.main]

    /**
     * This method completes the multipart upload request by collating all the
     * upload parts.
     *
     * @param s3Client    The S3 client used to interact with S3
     * @param bucketName  The name of the directory bucket
     * @param objectKey   The key (name) of the object to be uploaded
     * @param uploadId    The upload ID used to track the multipart upload
     * @param uploadParts The list of completed parts
     * @return True if the multipart upload is successfully completed, false
     *         otherwise
     */
    public static boolean completeDirectoryBucketMultipartUpload(S3Client s3Client, String bucketName, String objectKey,
            String uploadId, List<CompletedPart> uploadParts) {
        try {
            CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                    .parts(uploadParts)
                    .build();
            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .uploadId(uploadId)
                    .multipartUpload(completedMultipartUpload)
                    .build();

            CompleteMultipartUploadResponse response = s3Client.completeMultipartUpload(completeMultipartUploadRequest);
            logger.info("Multipart upload completed. ETag: {}", response.eTag());
            return true;
        } catch (S3Exception e) {
            logger.error("Failed to complete multipart upload: {} - Error code: {}", e.awsErrorDetails().errorMessage(),
                    e.awsErrorDetails().errorCode(), e);
            return false;
        }
    }
    // snippet-end:[s3directorybuckets.java2.completedirectorybucketmultipartupload.main]

    // Main method for testing
    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
        S3Client s3Client = createS3Client(region);
        String zone = "usw2-az1";
        String bucketName = "test-bucket-" + System.currentTimeMillis() + "--" + zone + "--x-s3";
        String uploadId;
        String objectKey = "largeObject";
        Path filePath = getFilePath("directoryBucket/sample-large-object.jpg");

        try {
            // Create the directory bucket
            createDirectoryBucket(s3Client, bucketName, zone);
            // Create a multipart upload
            uploadId = createDirectoryBucketMultipartUpload(s3Client, bucketName, objectKey);
            // Perform multipart upload for the directory bucket
            List<CompletedPart> uploadedParts = multipartUploadForDirectoryBucket(s3Client, bucketName, objectKey,
                    uploadId, filePath);
            logger.info("Uploaded parts: {}", uploadedParts);
            // Complete Multipart Uploads
            boolean completed = completeDirectoryBucketMultipartUpload(s3Client, bucketName, objectKey, uploadId,
                    uploadedParts);
            if (completed) {
                logger.info("Multipart upload successfully completed for bucket: {}", bucketName);
            } else {
                logger.error("Failed to complete multipart upload for bucket: {}", bucketName);
            }
        } catch (S3Exception e) {
            logger.error("An error occurred during S3 operations: {} - Error code: {}",
                    e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
        } catch (IOException e) {
            logger.error("An I/O error occurred: {}", e.getMessage());
        } finally {
            // Error handling
            try {
                logger.info("Starting cleanup for bucket: {}", bucketName);
                S3DirectoryBucketUtils.abortDirectoryBucketMultipartUploads(s3Client, bucketName);
                deleteAllObjectsInDirectoryBucket(s3Client, bucketName);
                deleteDirectoryBucket(s3Client, bucketName);
                logger.info("Cleanup completed for bucket: {}", bucketName);
            } catch (S3Exception e) {
                logger.error("Error during cleanup: {} - Error code: {}", e.awsErrorDetails().errorMessage(),
                        e.awsErrorDetails().errorCode(), e);
            } catch (RuntimeException e) {
                logger.error("Unexpected error during cleanup: {}", e.getMessage(), e);
            } finally {
                // Close the S3 client
                s3Client.close();
            }
        }
    }
}
