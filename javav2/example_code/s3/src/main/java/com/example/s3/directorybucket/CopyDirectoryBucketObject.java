// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.directorybucket;

// snippet-start:[s3directorybuckets.java2.copydirectorybucketobject.import]

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Path;

import static com.example.s3.util.S3DirectoryBucketUtils.createDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.createS3Client;
import static com.example.s3.util.S3DirectoryBucketUtils.deleteAllObjectsInDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.deleteDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.getFilePath;
import static com.example.s3.util.S3DirectoryBucketUtils.putDirectoryBucketObject;
// snippet-end:[s3directorybuckets.java2.copydirectorybucketobject.import]

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

public class CopyDirectoryBucketObject {
    private static final Logger logger = LoggerFactory.getLogger(CopyDirectoryBucketObject.class);

    // snippet-start:[s3directorybuckets.java2.copydirectorybucketobject.main]
    /**
     * Copies an object from one S3 general purpose bucket to one S3 directory
     * bucket.
     *
     * @param s3Client     The S3 client used to interact with S3
     * @param sourceBucket The name of the source bucket
     * @param objectKey    The key (name) of the object to be copied
     * @param targetBucket The name of the target bucket
     */
    public static void copyDirectoryBucketObject(S3Client s3Client, String sourceBucket, String objectKey,
            String targetBucket) {
        logger.info("Copying object: {} from bucket: {} to bucket: {}", objectKey, sourceBucket, targetBucket);

        try {
            // Create a CopyObjectRequest
            CopyObjectRequest copyReq = CopyObjectRequest.builder()
                    .sourceBucket(sourceBucket)
                    .sourceKey(objectKey)
                    .destinationBucket(targetBucket)
                    .destinationKey(objectKey)
                    .build();

            // Copy the object
            CopyObjectResponse copyRes = s3Client.copyObject(copyReq);
            logger.info("Successfully copied {} from bucket {} into bucket {}. CopyObjectResponse: {}",
                    objectKey, sourceBucket, targetBucket, copyRes.copyObjectResult().toString());

        } catch (S3Exception e) {
            logger.error("Failed to copy object: {} - Error code: {}", e.awsErrorDetails().errorMessage(),
                    e.awsErrorDetails().errorCode(), e);
            throw e;
        }
    }
    // snippet-end:[s3directorybuckets.java2.copydirectorybucketobject.main]

    // Main method for testing
    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
        S3Client s3Client = createS3Client(region);
        String zone = "usw2-az1";
        String sourceDirectoryBucket = "test-source-bucket-" + System.currentTimeMillis() + "--" + zone + "--x-s3";
        String targetDirectoryBucket = "test-destination-bucket-" + System.currentTimeMillis() + "--" + zone + "--x-s3";
        String objectKey = "example-object"; // your-object-key
        Path filePath = getFilePath("directoryBucket/sample1.txt");

        try {
            // Create the source and target directory buckets
            createDirectoryBucket(s3Client, sourceDirectoryBucket, zone);
            createDirectoryBucket(s3Client, targetDirectoryBucket, zone);
            // Put an object in the source bucket
            putDirectoryBucketObject(s3Client, sourceDirectoryBucket, objectKey, filePath);
            // Copy object from the source directory bucket to the target directory bucket
            copyDirectoryBucketObject(s3Client, sourceDirectoryBucket, objectKey, targetDirectoryBucket);
        } catch (S3Exception e) {
            logger.error("An error occurred during S3 operations: {} - Error code: {}",
                    e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode(), e);
        } finally {
            // Error handling
            try {
                logger.info("Starting cleanup for buckets: {} and {}", sourceDirectoryBucket, targetDirectoryBucket);
                deleteAllObjectsInDirectoryBucket(s3Client, sourceDirectoryBucket);
                deleteAllObjectsInDirectoryBucket(s3Client, targetDirectoryBucket);
                deleteDirectoryBucket(s3Client, sourceDirectoryBucket);
                deleteDirectoryBucket(s3Client, targetDirectoryBucket);
                logger.info("Cleanup completed for buckets: {} and {}", sourceDirectoryBucket, targetDirectoryBucket);
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
