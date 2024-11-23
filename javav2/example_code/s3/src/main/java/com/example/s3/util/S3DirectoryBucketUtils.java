// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.s3.util;

import com.example.s3.directorybucket.CompleteDirectoryBucketMultipartUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.CreateKeyRequest;
import software.amazon.awssdk.services.kms.model.CreateKeyResponse;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.ScheduleKeyDeletionRequest;
import software.amazon.awssdk.services.kms.model.ScheduleKeyDeletionResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.BucketInfo;
import software.amazon.awssdk.services.s3.model.BucketType;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DataRedundancy;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.LocationInfo;
import software.amazon.awssdk.services.s3.model.LocationType;
import software.amazon.awssdk.services.s3.model.MultipartUpload;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionByDefault;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionConfiguration;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionRule;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class S3DirectoryBucketUtils {
    private static final Logger logger = LoggerFactory.getLogger(S3DirectoryBucketUtils.class);

    /**
     * Creates an S3 client with the specified region.
     *
     * @param region The AWS region.
     * @return The S3 client.
     */
    public static S3Client createS3Client(Region region) {
        return S3Client.builder().region(region).build();
    }

    /**
     * Creates a new S3 directory bucket.
     * <p>
     * Once the CreateBucket API call returns successfully, the bucket exists and is ready for immediate use. You don't
     * need to use S3Waiter to wait for the bucket to exist before performing operations on the bucket.
     *
     * @param s3Client   The S3 client used to create the bucket
     * @param bucketName The name of the bucket to be created
     * @param zone       The region where the bucket will be created
     */
    public static void createDirectoryBucket(S3Client s3Client, String bucketName, String zone) {
        logger.info("Creating bucket: {}", bucketName);

        CreateBucketConfiguration bucketConfiguration = CreateBucketConfiguration.builder()
                .location(LocationInfo.builder()
                        .type(LocationType.AVAILABILITY_ZONE)
                        .name(zone).build())
                .bucket(BucketInfo.builder()
                        .type(BucketType.DIRECTORY)
                        .dataRedundancy(DataRedundancy.SINGLE_AVAILABILITY_ZONE)
                        .build())
                .build();

        try {
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .createBucketConfiguration(bucketConfiguration).build();
            CreateBucketResponse response = s3Client.createBucket(bucketRequest);
            logger.info("Bucket created successfully with location: {}", response.location());
        } catch (S3Exception e) {
            logger.error("Error creating bucket: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Checks if the specified bucket exists.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the bucket to check
     * @return true if the bucket exists, false otherwise
     */
    public static boolean checkBucketExists(S3Client s3Client, String bucketName) {
        logger.info("Checking if bucket exists: {}", bucketName);
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder().bucket(bucketName).build();
            s3Client.headBucket(headBucketRequest);
            logger.info("Amazon S3 directory bucket: \"{}\" found.", bucketName);
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                logger.warn("Amazon S3 directory bucket: \"{}\" not found.", bucketName);
                return false;
            } else {
                logger.error("Failed to access bucket: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
                throw e;
            }
        }
    }

    /**
     * Deletes the specified bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the bucket to delete
     */
    public static void deleteDirectoryBucket(S3Client s3Client, String bucketName) {
        logger.info("Deleting bucket: {}", bucketName);

        try {
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
            s3Client.deleteBucket(deleteBucketRequest);
            logger.info("Bucket deleted successfully.");
        } catch (S3Exception e) {
            logger.error("Error deleting bucket: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Sets the bucket policy for the specified S3 directory bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the directory bucket
     * @param policyText The policy text to be applied
     */
    public static void putDirectoryBucketPolicy(S3Client s3Client, String bucketName, String policyText) {
        logger.info("Setting policy on bucket: {}", bucketName);

        try {
            PutBucketPolicyRequest policyReq = PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(policyText)
                    .build();
            s3Client.putBucketPolicy(policyReq);
            logger.info("Bucket policy set successfully!");
        } catch (S3Exception e) {
            logger.error("Failed to set bucket policy: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Gets the bucket policy for the specified S3 directory bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the bucket
     * @return The bucket policy as a string
     */
    public static String getDirectoryBucketPolicy(S3Client s3Client, String bucketName) {
        logger.info("Retrieving policy for bucket: {}", bucketName);
        try {
            GetBucketPolicyRequest policyRequest = GetBucketPolicyRequest.builder().bucket(bucketName).build();
            GetBucketPolicyResponse policyResponse = s3Client.getBucketPolicy(policyRequest);
            String policyText = policyResponse.policy();
            logger.info("Retrieved policy for bucket: {}", bucketName);
            return policyText;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                logger.warn("No policy found for bucket: {}", bucketName);
                return null;
            } else {
                logger.error("Failed to retrieve policy for bucket: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
                throw e;
            }
        }
    }

    /**
     * Retrieves the encryption type for the specified S3 bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the bucket
     * @return The type of server-side encryption applied to the bucket (e.g., AES256, aws:kms)
     */
    public static String getBucketEncryptionType(S3Client s3Client, String bucketName) {
        try {
            // Create a request to get the bucket encryption configuration
            GetBucketEncryptionRequest getRequest = GetBucketEncryptionRequest.builder().bucket(bucketName).build();

            // Retrieve the bucket encryption response
            GetBucketEncryptionResponse getResponse = s3Client.getBucketEncryption(getRequest);

            // Get the server-side encryption rule from the response
            ServerSideEncryptionRule rule = getResponse.serverSideEncryptionConfiguration().rules().get(0);

            // Return the type of server-side encryption applied to the bucket
            return rule.applyServerSideEncryptionByDefault().sseAlgorithmAsString();
        } catch (S3Exception e) {
            logger.error("Failed to retrieve encryption for bucket: {} - Error message: {}- Error code: {}", bucketName, e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Sets the default encryption configuration for an S3 bucket as SSE-KMS.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the directory bucket
     * @param kmsKeyId   The ID of the customer-managed KMS key
     */
    public static void putDirectoryBucketEncryption(S3Client s3Client, String bucketName, String kmsKeyId) {
        // Define the default encryption configuration to use SSE-KMS. For directory buckets, AWS managed KMS keys aren't supported. Only customer-managed keys are supported.
        ServerSideEncryptionByDefault encryptionByDefault = ServerSideEncryptionByDefault.builder()
                .sseAlgorithm(ServerSideEncryption.AWS_KMS)
                .kmsMasterKeyID(kmsKeyId)
                .build();

        // Create a server-side encryption rule to apply the default encryption configuration. For directory buckets, the bucketKeyEnabled field is enforced to be true.
        ServerSideEncryptionRule rule = ServerSideEncryptionRule.builder()
                .bucketKeyEnabled(true)
                .applyServerSideEncryptionByDefault(encryptionByDefault)
                .build();

        // Create the server-side encryption configuration for the bucket
        ServerSideEncryptionConfiguration encryptionConfiguration = ServerSideEncryptionConfiguration.builder()
                .rules(rule)
                .build();

        // Create the PutBucketEncryption request
        PutBucketEncryptionRequest putRequest = PutBucketEncryptionRequest.builder()
                .bucket(bucketName)
                .serverSideEncryptionConfiguration(encryptionConfiguration)
                .build();

        // Set the bucket encryption
        try {
            s3Client.putBucketEncryption(putRequest);
            logger.info("SSE-KMS bucket encryption configuration set for the directory bucket: {}", bucketName);
        } catch (S3Exception e) {
            logger.error("Failed to set bucket encryption: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }



    /**
     * Puts an object into the specified S3 directory bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the directory bucket
     * @param objectKey  The key (name) of the object to be placed in the bucket
     * @param filePath   The path of the file to be uploaded
     */
    public static void putDirectoryBucketObject(S3Client s3Client, String bucketName, String objectKey, Path filePath) {
        logger.info("Putting object: {} into bucket: {}", objectKey, bucketName);

        try {
            PutObjectRequest putObj = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            s3Client.putObject(putObj, filePath);
            logger.info("Successfully placed {} into bucket {}", objectKey, bucketName);
        } catch (UncheckedIOException e) {
            throw S3Exception.builder().message("Failed to read the file: " + e.getMessage()).cause(e)
                    .awsErrorDetails(AwsErrorDetails.builder()
                            .errorCode("ClientSideException:FailedToReadFile")
                            .errorMessage(e.getMessage())
                            .build())
                    .build();
        } catch (S3Exception e) {
            logger.error("Failed to put object: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Checks if the specified S3 bucket exists.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the bucket to check
     * @return True if the bucket exists, false otherwise
     */
    public static boolean doesBucketExist(S3Client s3Client, String bucketName) {
        try {
            // Attempt to retrieve the bucket metadata. If this request succeeds, the bucket exists
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true; // If no exception is thrown, the bucket exists
        } catch (NoSuchBucketException e) {
            return false; // If NoSuchBucketException is thrown, the bucket does not exist
        } catch (S3Exception e) {
            logger.error("Failed to check if bucket exists: {} - Error message: {} -Error code: {}", bucketName, e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Checks if the specified object exists in the given S3 directory bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the directory bucket
     * @param objectKey  The key of the object to check
     * @return True if the object exists, false otherwise
     */
    public static boolean checkObjectExists(S3Client s3Client, String bucketName, String objectKey) {
        try {
            // Attempt to retrieve the object's metadata. If this request succeeds, the object exists
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(objectKey).build());
            return true; // If no exception is thrown, the object exists
        } catch (NoSuchKeyException e) {
            return false; // If NoSuchKeyException is thrown, the object does not exist
        } catch (S3Exception e) {
            logger.error("Failed to check if bucket exists: {} - Error message: {} -Error code: {}", objectKey, e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Deletes an object from the specified S3 bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the S3 bucket
     * @param objectKey  The key (name) of the object to be deleted
     */
    public static void deleteObject(S3Client s3Client, String bucketName, String objectKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Successfully deleted object: {} from bucket: {}", objectKey, bucketName);
        } catch (S3Exception e) {
            logger.error("Error deleting object: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Deletes all objects in the specified S3 directory bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the directory bucket to be emptied
     */
    public static void deleteAllObjectsInDirectoryBucket(S3Client s3Client, String bucketName) {
        try {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response listObjectsResponse;
            do {
                listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
                List<S3Object> objects = listObjectsResponse.contents();

                for (S3Object object : objects) {
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(object.key())
                            .build();
                    s3Client.deleteObject(deleteObjectRequest);
                }

                listObjectsRequest = listObjectsRequest.toBuilder()
                        .continuationToken(listObjectsResponse.nextContinuationToken())
                        .build();
            } while (listObjectsResponse.isTruncated());

            logger.info("Successfully deleted all objects in bucket: {}", bucketName);
        } catch (S3Exception e) {
            logger.error("Failed to delete objects in bucket: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Method to get AWS account ID
     * <p>
     * This method uses the AWS Security Token Service (STS) to get the
     * account ID of the current AWS account. It builds an STS client,
     * sends a GetCallerIdentity request, and retrieves the account ID
     * from the response.
     *
     * @return The AWS account ID
     */
    public static String getAwsAccountId() {
        try (StsClient stsClient = StsClient.builder().region(Region.US_WEST_2).build()) {
            GetCallerIdentityRequest request = GetCallerIdentityRequest.builder().build();
            GetCallerIdentityResponse response = stsClient.getCallerIdentity(request);
            return response.account();
        }
    }

    /**
     * Creates a new KMS customer-managed key.
     *
     * @param kmsClient The KMS client used to create the key
     * @return The ID of the created KMS key
     */
    public static String createKmsKey(KmsClient kmsClient) {
        CreateKeyRequest request = CreateKeyRequest.builder()
                .description("Customer managed key for S3 bucket encryption")
                .keyUsage("ENCRYPT_DECRYPT")
                .build();

        CreateKeyResponse response = kmsClient.createKey(request);
        return response.keyMetadata().keyId();
    }

    /**
     * Creates a KMS client with the specified region.
     *
     * @param region The AWS region
     * @return The KMS client
     */
    public static KmsClient createKmsClient(Region region) {
        return KmsClient.builder().region(region).build();
    }

    /**
     * Schedules the deletion of the specified customer managed key (CMK).
     *
     * @param kmsClient           The KMS client used to interact with KMS
     * @param keyId               The ID of the CMK to be deleted
     * @param waitingPeriodInDays The waiting period (in days) before the key is
     *                            permanently deleted
     * @return The scheduled deletion date
     */
    public static String scheduleKeyDeletion(KmsClient kmsClient, String keyId, int waitingPeriodInDays) {
        logger.info("Scheduling deletion for key: {}", keyId);

        try {
            ScheduleKeyDeletionRequest request = ScheduleKeyDeletionRequest.builder()
                    .keyId(keyId)
                    .pendingWindowInDays(waitingPeriodInDays)
                    .build();

            ScheduleKeyDeletionResponse response = kmsClient.scheduleKeyDeletion(request);
            logger.info("Successfully scheduled key deletion. Deletion date: {}", response.deletionDate());
            return response.deletionDate().toString();
        } catch (KmsException e) {
            logger.error("Failed to schedule key deletion: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * Creates and returns an S3Presigner object
     * using the specified AWS region.
     *
     * @param region The AWS region to be used for the S3Presigner
     * @return A newly instantiated S3Presigner object
     */
    public static S3Presigner createS3Presigner(Region region) {
        return S3Presigner.builder()
                .region(region)
                .build();
    }

    /**
     * Aborts multipart uploads for the specified S3 directory bucket.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the directory bucket
     */
    public static void abortDirectoryBucketMultipartUploads(S3Client s3Client, String bucketName) {
        logger.info("Aborting multipart uploads for bucket: {}", bucketName);

        try {
            ListMultipartUploadsRequest listMultipartUploadsRequest = ListMultipartUploadsRequest.builder()
                    .bucket(bucketName)
                    .build();

            ListMultipartUploadsResponse listMultipartUploadsResponse = s3Client
                    .listMultipartUploads(listMultipartUploadsRequest);
            List<MultipartUpload> uploads = listMultipartUploadsResponse.uploads();

            for (MultipartUpload upload : uploads) {
                AbortMultipartUploadRequest abortMultipartUploadRequest = AbortMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(upload.key())
                        .uploadId(upload.uploadId())
                        .build();
                s3Client.abortMultipartUpload(abortMultipartUploadRequest);
                logger.info("Aborted multipart upload: {} for object: {}", upload.uploadId(), upload.key());
            }
        } catch (S3Exception e) {
            logger.error("Failed to abort all multipart uploads: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * This method creates a multipart upload request that generates a unique upload
     * ID used to track all the upload parts.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the directory bucket
     * @param objectKey  The key (name) of the object to be uploaded
     * @return The upload ID used to track the multipart upload
     */
    public static String createDirectoryBucketMultipartUpload(S3Client s3Client, String bucketName, String objectKey) {
        logger.info("Creating multipart upload for object: {} in bucket: {}", objectKey, bucketName);

        try {
            CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);
            String uploadId = response.uploadId();
            logger.info("Multipart upload initiated. Upload ID: {}", uploadId);
            return uploadId;
        } catch (S3Exception e) {
            logger.error("Failed to create multipart upload: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    /**
     * This method creates part requests and uploads individual parts to S3.
     * While it uses the UploadPart API to upload a single part, it does so
     * sequentially to handle multiple parts of a file, returning all the uploaded
     * parts.
     *
     * @param s3Client   The S3 client used to interact with S3
     * @param bucketName The name of the directory bucket
     * @param objectKey  The key (name) of the object to be uploaded
     * @param uploadId   The upload ID used to track the multipart upload
     * @param filePath   The path to the file to be uploaded
     * @return A list of completed parts
     * @throws IOException if an I/O error occurs
     */
    public static List<CompletedPart> multipartUploadForDirectoryBucket(S3Client s3Client, String bucketName,
                                                                        String objectKey, String uploadId, Path filePath) throws IOException {
        logger.info("Uploading parts for object: {} in bucket: {}", objectKey, bucketName);

        int partNumber = 1;
        List<CompletedPart> uploadedParts = new ArrayList<>();
        ByteBuffer bb = ByteBuffer.allocate(1024 * 1024 * 5); // 5 MB byte buffer

        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
            long fileSize = file.length();
            int position = 0;

            while (position < fileSize) {
                file.seek(position);
                int read = file.getChannel().read(bb);

                bb.flip(); // Swap position and limit before reading from the buffer
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build();

                UploadPartResponse partResponse = s3Client.uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(bb));

                CompletedPart uploadedPart = CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(partResponse.eTag())
                        .build();

                uploadedParts.add(uploadedPart);

                logger.info("Uploaded part number: {} with ETag: {}", partNumber, partResponse.eTag());

                bb.clear();
                position += read;
                partNumber++;
            }
        } catch (IOException e) {
            logger.error("Error reading file: {}", e.getMessage());
            throw e;
        } catch (S3Exception e) {
            logger.error("Failed to upload part: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
        return uploadedParts;
    }

    /**
     * This method completes the multipart upload request by collating all the
     * upload parts.
     *
     * @param s3Client    The S3 client used to interact with S3
     * @param bucketName  The name of the directory bucket
     * @param objectKey   The key (name) of the object to be uploaded
     * @param uploadId    The upload ID used to track the multipart upload
     * @param uploadParts The list of completed parts
     */
    public static void completeDirectoryBucketMultipartUpload(S3Client s3Client, String bucketName, String objectKey,
                                                              String uploadId, List<CompletedPart> uploadParts) {
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(uploadParts)
                .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .uploadId(uploadId)
                .multipartUpload(completedMultipartUpload)
                .build();

        try {
            CompleteMultipartUploadResponse response = s3Client.completeMultipartUpload(completeMultipartUploadRequest);
            logger.info("Multipart upload completed. Object Key: {} ETag: {}", response.key(), response.eTag());
        } catch (S3Exception e) {
            logger.error("Failed to complete multipart upload: {} - Error code: {}", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    public static Path getFilePath(String pathRelativeToResourcesDir) {
        Path filePath = null;
        try {
            filePath = Paths.get(S3DirectoryBucketUtils.class.getClassLoader()
                    .getResource(pathRelativeToResourcesDir)
                    .toURI());
        } catch (URISyntaxException e) {
            logger.error("Error getting file path: {}", e.getMessage());
        }
        return filePath;
    }
}
