// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.replication;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sts.StsClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */
public class BucketReplication {

    public static Scanner scanner = new Scanner(System.in);

    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    public static void main(String[] args) throws IOException {
        final String usage = """
            Usage:
               <roleName> <bucketRegion> <sourceBucketName> <destBucketName> 

            Where:
                roleName - The name of the IAM role that will be used to access the S3 buckets.
                bucketRegion - The AWS region where the S3 buckets are located.
                sourceBucketName - The name of the S3 bucket that contains the object to be copied.
                destBucketName - The name of the S3 bucket to which the object will be copied.
            """;

        if (args.length != 4) {
            System.out.println(usage);
            return;
        }

        String accountId = getAccountNumber();
        String roleName = args[0];
        String bucketRegion = args[1];
        String sourceBucketName = args[2];
        String destBucketName = args[3];
        String roleARN = "arn:aws:iam::" + accountId + ":role/" + roleName;
        String destinationBucketARN = "arn:aws:s3:::" + destBucketName;

        S3Client s3Client = S3Client.builder()
            .region(Region.of(bucketRegion))
            .build();

        IamClient iamClient = IamClient.builder()
            .region(Region.US_EAST_1)
            .build();

        System.out.println(DASHES);
        System.out.println("""
            The provided code is a Java application that demonstrates the implementation of 
            Cross-Region Replication (CRR) for Amazon S3 buckets. The application performs 
            the following steps:
                        
            The application starts by initializing the necessary AWS clients, such as S3Client and 
            IamClient, to interact with the AWS services. It then creates two S3 buckets, 
            one as the source bucket and the other as the destination bucket. Next, it 
            assigns an IAM role with the necessary permissions to access the source and destination 
            buckets. 
            
            The application then enables bucket versioning for both the source and destination buckets, 
            which is a prerequisite for enabling CRR. Finally, the application sets the replication 
            configuration for the source bucket, specifying the destination bucket, the IAM role, 
            and the replication rules.
            
            Lets get started...
                                 
            """);

        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("1. Create a source and destination bucket");
        waitForInputToContinue(scanner);
        createBucket(s3Client, sourceBucketName);
        createBucket(s3Client, destBucketName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Assign a role");
        waitForInputToContinue(scanner);
        assignRole(iamClient, roleName, sourceBucketName, destBucketName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Enable bucket versioning");
        waitForInputToContinue(scanner);
        enableBucketVersioning(s3Client, sourceBucketName);
        enableBucketVersioning(s3Client, destBucketName);
        System.out.println(DASHES);

        System.out.println("4. Set replication on both buckets");
        waitForInputToContinue(scanner);
        setReplication(s3Client, sourceBucketName, destBucketName, destinationBucketARN, roleARN);
        System.out.println(DASHES);

        System.out.println("5. Get replication information");
        waitForInputToContinue(scanner);
        getReplicationDetails(s3Client, sourceBucketName);
        System.out.println(DASHES);

        System.out.println("6. Delete the AWS resources");
        waitForInputToContinue(scanner);
        deleteBucket(s3Client, sourceBucketName);
        deleteBucket(s3Client, destBucketName);
        System.out.println("This concludes the Cross-Region Replication example");
        System.out.println(DASHES);
    }

    // snippet-start:[s3.java2.bucket_enable.version.main]
    /**
     * Enables bucket versioning for the specified S3 bucket.
     *
     * @param s3Client the S3 client to use for the operation
     * @param bucketName the name of the S3 bucket to enable versioning for
     */
    public static void enableBucketVersioning(S3Client s3Client, String bucketName){
        VersioningConfiguration versioningConfiguration = VersioningConfiguration.builder()
            .status(BucketVersioningStatus.ENABLED)
            .build();

        PutBucketVersioningRequest versioningRequest = PutBucketVersioningRequest.builder()
            .bucket(bucketName)
            .versioningConfiguration(versioningConfiguration)
            .build();

        s3Client.putBucketVersioning(versioningRequest);
        System.out.println("Bucket versioning has been enabled for "+bucketName);
    }
    // snippet-end:[s3.java2.bucket_enable.version.main]

    // snippet-start:[s3.java2.bucket_replication.main]
    /**
     * Sets the replication configuration for an Amazon S3 bucket.
     *
     * @param s3Client             the S3Client instance to use for the operation
     * @param sourceBucketName     the name of the source bucket
     * @param destBucketName       the name of the destination bucket
     * @param destinationBucketARN the Amazon Resource Name (ARN) of the destination bucket
     * @param roleARN              the ARN of the IAM role to use for the replication configuration
     */
    public static void setReplication(S3Client s3Client, String sourceBucketName, String destBucketName, String destinationBucketARN, String roleARN) {
        try {
            Destination destination = Destination.builder()
                .bucket(destinationBucketARN)
                .storageClass(StorageClass.STANDARD)
                .build();

            // Define a prefix filter for replication.
            ReplicationRuleFilter ruleFilter = ReplicationRuleFilter.builder()
                .prefix("documents/")
                .build();

            // Define delete marker replication setting.
            DeleteMarkerReplication deleteMarkerReplication = DeleteMarkerReplication.builder()
                .status(DeleteMarkerReplicationStatus.DISABLED)
                .build();

            // Create the replication rule.
            ReplicationRule replicationRule = ReplicationRule.builder()
                .priority(1)
                .filter(ruleFilter)
                .status(ReplicationRuleStatus.ENABLED)
                .deleteMarkerReplication(deleteMarkerReplication)
                .destination(destination)
                .build();

            List<ReplicationRule> replicationRuleList = new ArrayList<>();
            replicationRuleList.add(replicationRule);

            // Define the replication configuration with IAM role.
            ReplicationConfiguration configuration = ReplicationConfiguration.builder()
                .role(roleARN)
                .rules(replicationRuleList)
                .build();

            // Apply the replication configuration to the source bucket.
            PutBucketReplicationRequest replicationRequest = PutBucketReplicationRequest.builder()
                .bucket(sourceBucketName)
                .replicationConfiguration(configuration)
                .build();

            s3Client.putBucketReplication(replicationRequest);
            System.out.println("Replication configuration set successfully.");

        } catch (IllegalArgumentException e) {
            System.err.println("Configuration error: " + e.getMessage());
        } catch (S3Exception e) {
            System.err.println("S3 Exception: " + e.awsErrorDetails().errorMessage());
            System.err.println("Status Code: " + e.statusCode());
            System.err.println("Error Code: " + e.awsErrorDetails().errorCode());


        } catch (SdkException e) {
            System.err.println("SDK Exception: " + e.getMessage());
        }
    }
    // snippet-end:[s3.java2.bucket_replication.main]

    // snippet-start:[s3.java2.bucket_get.replication.main]
    /**
     * Retrieves the replication details for the specified S3 bucket.
     *
     * @param s3Client           the S3 client used to interact with the S3 service
     * @param sourceBucketName   the name of the S3 bucket to retrieve the replication details for
     *
     * @throws S3Exception if there is an error retrieving the replication details
     */
    public static void getReplicationDetails(S3Client s3Client, String sourceBucketName) {
        GetBucketReplicationRequest getRequest = GetBucketReplicationRequest.builder()
            .bucket(sourceBucketName)
            .build();

        try {
            ReplicationConfiguration replicationConfig = s3Client.getBucketReplication(getRequest).replicationConfiguration();
            ReplicationRule rule = replicationConfig.rules().get(0);
            System.out.println("Retrieved destination bucket: " + rule.destination().bucket());
            System.out.println("Retrieved priority: " + rule.priority());
            System.out.println("Retrieved source-bucket replication rule status: " + rule.status());

        } catch (S3Exception e) {
            System.err.println("Failed to retrieve replication details: " + e.awsErrorDetails().errorMessage());
        }
    }
    // snippet-end:[s3.java2.bucket_get.replication.main]

    /**
     * Creates an Amazon S3 bucket.
     *
     * @param s3Client    the S3 client to use for the bucket creation
     * @param bucketName  the name of the bucket to create
     */
    public static void createBucket(S3Client s3Client, String bucketName) {
        CreateBucketRequest bucketRequest;
        bucketRequest = CreateBucketRequest.builder()
            .bucket(bucketName)
            .build();

        s3Client.createBucket(bucketRequest);

        // Wait until the bucket exists.
        S3Waiter s3Waiter = s3Client.waiter();
        HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
            .bucket(bucketName)
            .build();

        WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
        waiterResponse.matched().response().ifPresent(headBucketResponse -> {
            System.out.println(bucketName + " is ready");
        });
    }

    /**
     * Assigns a role with the specified name and grants the necessary permissions to
     * access the source and destination S3 buckets.
     *
     * @param iamClient          the AWS IAM client used to perform the role and policy
     *                           management operations
     * @param roleName           the name of the role to be created
     * @param sourceBucket       the name of the source S3 bucket
     * @param destinationBucket  the name of the destination S3 bucket
     */
    public static void assignRole(IamClient iamClient, String roleName, String sourceBucket, String destinationBucket) {
        String trustPolicy = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {
                            "Service": "s3.amazonaws.com"
                        },
                        "Action": "sts:AssumeRole"
                    }
                ]
            }
            """;

        CreateRoleRequest createRoleRequest = CreateRoleRequest.builder()
            .roleName(roleName)
            .assumeRolePolicyDocument(trustPolicy)
            .build();

        iamClient.createRole(createRoleRequest);

        String permissionPolicy = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Action": "s3:*",
                        "Resource": [
                            "arn:aws:s3:::%s",
                            "arn:aws:s3:::%s/*"
                        ]
                    }
                ]
            }
            """.formatted(sourceBucket, destinationBucket);

        PutRolePolicyRequest putRolePolicyRequest = PutRolePolicyRequest.builder()
            .roleName(roleName)
            .policyDocument(permissionPolicy)
            .policyName("crrRolePolicy")
            .build();

        iamClient.putRolePolicy(putRolePolicyRequest);
        System.out.println("The policy  was added to the role");
    }

    /**
     * Deletes an Amazon S3 bucket.
     *
     * @param s3Client the S3Client object used to interact with the Amazon S3 service
     * @param bucket the name of the bucket to be deleted
     *
     * @throws S3Exception if an error occurs while deleting the bucket
     */
    public static void deleteBucket(S3Client s3Client, String bucket) {
        try {
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucket)
                .build();

            s3Client.deleteBucket(deleteBucketRequest);
            System.out.println(bucket + " was deleted.");

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    /**
     * Retrieves the account number of the current AWS user using the AWS STS (Security Token Service)
     * client.
     *
     * @return the account number of the current AWS user as a String
     */
    public static String getAccountNumber() {
        StsClient stsClient = StsClient.create();
        return stsClient.getCallerIdentity().account();
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else {
                // Handle invalid input.
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
}