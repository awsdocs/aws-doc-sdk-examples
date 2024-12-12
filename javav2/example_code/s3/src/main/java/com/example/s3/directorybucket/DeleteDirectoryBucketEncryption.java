// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.directorybucket;

// snippet-start:[s3directorybuckets.java2.delete_directory_bucket_encryption.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.example.s3.util.S3DirectoryBucketUtils.createDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.createS3Client;
import static com.example.s3.util.S3DirectoryBucketUtils.deleteDirectoryBucket;
// snippet-end:[s3directorybuckets.java2.delete_directory_bucket_encryption.import]

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

public class DeleteDirectoryBucketEncryption {
    private static final Logger logger = LoggerFactory.getLogger(DeleteDirectoryBucketEncryption.class);

    // snippet-start:[s3directorybuckets.java2.directory_bucket_delete_bucket_encryption.main]
    /**
     * Deletes the encryption configuration from an S3 bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the directory bucket
     */
    public static void deleteDirectoryBucketEncryption(S3Client s3Client, String bucketName) {
        DeleteBucketEncryptionRequest deleteRequest = DeleteBucketEncryptionRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            s3Client.deleteBucketEncryption(deleteRequest);
            logger.info("Bucket encryption deleted for bucket: {}", bucketName);
        } catch (S3Exception e) {
            logger.error("Failed to delete bucket encryption: {} - Error code: {}", e.awsErrorDetails().errorMessage(),
                    e.awsErrorDetails().errorCode(), e);
            throw e;
        }
    }
    // snippet-end:[s3directorybuckets.java2.directory_bucket_delete_bucket_encryption.main]

    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
        S3Client s3Client = createS3Client(region);
        String zone = "usw2-az1";
        String bucketName = "test-bucket-" + System.currentTimeMillis() + "--" + zone + "--x-s3";

        try {
            // Create the directory bucket
            createDirectoryBucket(s3Client, bucketName, zone);
            // Delete encryption from the directory bucket
            deleteDirectoryBucketEncryption(s3Client, bucketName);
        } catch (S3Exception e) {
            logger.error("An error occurred during S3 operations: {} - Error code: {}",
                    e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode(), e);
        } finally {
            try {
                // Delete the bucket
                deleteDirectoryBucket(s3Client, bucketName);
            } catch (S3Exception e) {
                logger.error("Failed to delete the bucket: {} - Error code: {}", e.awsErrorDetails().errorMessage(),
                        e.awsErrorDetails().errorCode(), e);
            } catch (RuntimeException e) {
                logger.error("Failed to delete the bucket due to unexpected error: {}", e.getMessage(), e);
            } finally {
                s3Client.close();
            }
        }
    }
}
