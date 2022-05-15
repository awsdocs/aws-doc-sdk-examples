//snippet-sourcedescription:[PutBucketLogging.java demonstrates how to set the logging parameters for an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[09/27/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[s3.java2.s3_put_log.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.TargetGrant;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[s3.java2.s3_put_log.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutBucketLogging {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "  <bucketName> <targetBucket>  \n\n" +
                "Where:\n" +
                "  bucketName - the Amazon S3 bucket to upload an object into.\n" +
                "  targetBucket - the target bucket .\n" ;

        if (args.length != 3) {
             System.out.println(USAGE);
             System.exit(1);
        }

        String bucketName = args[0];
        String targetBucket = args[1];
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        setlogRequest(s3, bucketName, targetBucket);
        s3.close();
    }

    // snippet-start:[s3.java2.s3_put_log.main]
    public static void setlogRequest(S3Client s3, String bucketName, String targetBucket) {

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
                    .expectedBucketOwner("814548047983")
                    .bucketLoggingStatus(loggingStatus)
                    .build();

            s3.putBucketLogging(loggingRequest);
            System.out.println("Enabling logging for the target bucket " + targetBucket);

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[s3.java2.s3_put_log.main]
}
