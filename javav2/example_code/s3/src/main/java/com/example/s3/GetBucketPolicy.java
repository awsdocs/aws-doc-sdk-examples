//snippet-sourcedescription:[GetBucketPolicy.java demonstrates how to get the bucket policy for an existing Amazon Simple Storage Service (Amazon S3) bucket]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.s3;

// snippet-start:[s3.java2.get_bucket_policy.import]
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyResponse;
// snippet-end:[s3.java2.get_bucket_policy.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GetBucketPolicy {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <bucketName>\n\n" +
                "Where:\n" +
                "    bucketName - The Amazon S3 bucket to get the policy from.\n\n" ;

          if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
         }

        String bucketName = args[0];
        System.out.format("Getting policy for bucket: \"%s\"\n\n", bucketName);
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        String polText = getPolicy(s3, bucketName );
        System.out.println("Policy Text: "+polText);
        s3.close();
    }

    // snippet-start:[s3.java2.get_bucket_policy.main]
    public static String getPolicy(S3Client s3, String bucketName) {

        String policyText;
        System.out.format("Getting policy for bucket: \"%s\"\n\n", bucketName);

        GetBucketPolicyRequest policyReq = GetBucketPolicyRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            GetBucketPolicyResponse policyRes = s3.getBucketPolicy(policyReq);
            policyText = policyRes.policy();
            return policyText;
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
// snippet-end:[s3.java2.get_bucket_policy.main]
}
