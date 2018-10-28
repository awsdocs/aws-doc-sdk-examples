//snippet-sourcedescription:[DeleteBucketPolicy.java demonstrates how to ...]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
package com.example.s3;

import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteBucketPolicyRequest;

/**
* Get the bucket policy from an existing S3 bucket.
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
*/
public class DeleteBucketPolicy
{
   public static void main(String[] args)
   {
      final String USAGE = "\n" +
         "Usage:\n" +
         "    DeleteBucketPolicy <bucket>\n\n" +
         "Where:\n" +
         "    bucket - the bucket to delete the policy from.\n\n" +
         "Example:\n" +
         "    DeleteBucketPolicy testbucket\n\n";

      if (args.length < 1) {
         System.out.println(USAGE);
         System.exit(1);
      }

      String bucket_name = args[0];
      String policy_text = null;

      System.out.format("Deleting policy from bucket: \"%s\"\n\n", bucket_name);

      Region region = Region.US_WEST_2;
      S3Client s3 = S3Client.builder().region(region).build();
      DeleteBucketPolicyRequest delReq = DeleteBucketPolicyRequest.builder()
    		  .bucket(bucket_name)
    		  .build();
      try {
         s3.deleteBucketPolicy(delReq);
      } catch (S3Exception e) {
         System.err.println(e.errorMessage());
         System.exit(1);
      }

      System.out.println("Done!");
   }
}


