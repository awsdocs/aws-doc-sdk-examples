//snippet-sourcedescription:[DeleteBucketPolicy.java demonstrates how to delete a policy from an Amazon S3 bucket.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/6/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at
http://aws.amazon.com/apache2.0/
This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/

package com.example.s3;
// snippet-start:[s3.java2.delete_bucket_policy.import]
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteBucketPolicyRequest;
// snippet-end:[s3.java2.delete_bucket_policy.import]

/**
 * Delete a bucket policy from an existing S3 bucket.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */

public class DeleteBucketPolicy {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteBucketPolicy <bucket>\n\n" +
                "Where:\n" +
                "    bucket - the bucket to delete the policy from (i.e., bucket1)\n\n" +
                "Example:\n" +
                "    bucket1\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucketName = args[0];
        System.out.format("Deleting policy from bucket: \"%s\"\n\n", bucketName);

        //Create a S3Client object
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();

        //Delete the bucket policy
        DeleteS3BucketPolicy(s3, bucketName);
    }
    // snippet-start:[s3.java2.delete_bucket_policy.main]
    public static void DeleteS3BucketPolicy(S3Client s3, String bucketName) {

        //Create a DeleteBucketPolicyRequest object
        DeleteBucketPolicyRequest delReq = DeleteBucketPolicyRequest.builder()
                .bucket(bucketName)
                .build();
        try {
            s3.deleteBucketPolicy(delReq);
            System.out.println("Done!");
           
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[s3.java2.delete_bucket_policy.main]
