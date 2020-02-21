//snippet-sourcedescription:[GetBucketPolicy.java demonstrates how to get the bucket policy for an existing S3 bucket]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2020-02-06]
//snippet-sourceauthor:[scmacdon]
/*
Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at
http://aws.amazon.com/apache2.0/
This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
package com.example.s3;
// snippet-start:[s3.java2.get_bucket_policy.complete]
// snippet-start:[s3.java2.get_bucket_policy.import]
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyResponse;
// snippet-end:[s3.java2.get_bucket_policy.import]

/**
 * Gets the bucket policy from an existing S3 bucket.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
// snippet-start:[s3.java2.get_bucket_policy.main]
public class GetBucketPolicy {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetBucketPolicy <bucket>\n\n" +
                "Where:\n" +
                "    bucket - the bucket to get the policy from.\n\n" +
                "Example:\n" +
                "    GetBucketPolicy bucket1\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucketName = args[0];
        String policyText = null;

        System.out.format("Getting policy for bucket: \"%s\"\n\n", bucketName);

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();

        GetBucketPolicyRequest policyReq = GetBucketPolicyRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            GetBucketPolicyResponse policyRes = s3.getBucketPolicy(policyReq);
            policyText = policyRes.policy();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        if (policyText == null) {
            System.out.println("The specified bucket has no bucket policy.");
        } else {
            System.out.println("Returned policy:");
            System.out.println("----");
            System.out.println(policyText);
            System.out.println("----\n");
        }

        System.out.println("Done!");
    }
}
// snippet-end:[s3.java2.get_bucket_policy.main]
// snippet-end:[s3.java2.get_bucket_policy.complete]
