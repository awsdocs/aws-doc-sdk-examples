// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

// snippet-start:[s3.java2.s3_put_log.main]
// snippet-start:[s3.java2.s3_put_log.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketAclRequest;
import software.amazon.awssdk.services.s3.model.BucketLogsPermission;
import software.amazon.awssdk.services.s3.model.Grantee;
import software.amazon.awssdk.services.s3.model.LoggingEnabled;
import software.amazon.awssdk.services.s3.model.Type;
import software.amazon.awssdk.services.s3.model.BucketLoggingStatus;
import software.amazon.awssdk.services.s3.model.PutBucketLoggingRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.TargetGrant;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[s3.java2.s3_put_log.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutBucketLogging {
    public static void main(String[] args) {
        final String usage = """

            Usage:
              <bucketName> <targetBucket> <accountId> \s

            Where:
              bucketName - The Amazon S3 bucket to upload an object into.
              targetBucket - The target bucket.
              accountId - The account id.
            """;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String bucketName = args[0];
        String targetBucket = args[1];
        String accountId = args[2];
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
            .region(region)
            .build();

        setlogRequest(s3, bucketName, targetBucket, accountId);
        s3.close();
    }

    /**
     * Enables logging for the specified S3 bucket.
     *
     * @param s3 an instance of the {@link S3Client} used to interact with the S3 service
     * @param bucketName the name of the bucket for which logging needs to be enabled
     * @param targetBucket the name of the target bucket where the logs will be stored
     * @param accountId the account Id
     *
     * @throws S3Exception if an error occurs while enabling logging for the bucket
     */
    public static void setlogRequest(S3Client s3, String bucketName, String targetBucket, String accountId) {
        try {
            GetBucketAclRequest aclRequest = GetBucketAclRequest.builder()
                .bucket(targetBucket)
                .build();

            s3.getBucketAcl(aclRequest);
            Grantee grantee = Grantee.builder()
                .type(Type.GROUP)
                .uri("http://acs.amazonaws.com/groups/s3/LogDelivery")
                .build();

            TargetGrant targetGrant = TargetGrant.builder()
                .grantee(grantee)
                .permission(BucketLogsPermission.FULL_CONTROL)
                .build();

            List<TargetGrant> granteeList = new ArrayList<>();
            granteeList.add(targetGrant);

            LoggingEnabled loggingEnabled = LoggingEnabled.builder()
                .targetBucket(targetBucket)
                .targetGrants(granteeList)
                .build();

            BucketLoggingStatus loggingStatus = BucketLoggingStatus.builder()
                .loggingEnabled(loggingEnabled)
                .build();

            PutBucketLoggingRequest loggingRequest = PutBucketLoggingRequest.builder()
                .bucket(bucketName)
                .expectedBucketOwner(accountId)
                .bucketLoggingStatus(loggingStatus)
                .build();

            s3.putBucketLogging(loggingRequest);
            System.out.println("Enabling logging for the target bucket " + targetBucket);

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[s3.java2.s3_put_log.main]
