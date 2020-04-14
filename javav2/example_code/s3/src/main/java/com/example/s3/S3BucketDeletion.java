//snippet-sourcedescription:[S3BucketDeletion.java demonstrates how to delete an empty S3 bucket and an S3 bucket that contains objects.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/6/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.s3;

// snippet-start:[s3.java2.bucket_deletion.import]
import software.amazon.awssdk.regions.Region;
// snippet-start:[s3.java2.s3_bucket_ops.delete_bucket.import]
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

// snippet-end:[s3.java2.s3_bucket_ops.delete_bucket.import]
// snippet-end:[s3.java2.bucket_deletion.import]
// snippet-start:[s3.java2.bucket_deletion.main]
public class S3BucketDeletion {

    private static S3Client s3;

    public static void main(String[] args) throws Exception {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    S3BucketDeletion <bucket>\n\n" +
                "Where:\n" +
                "    bucket - the bucket to delete  (i.e., bucket1)\n\n" +
                "Example:\n" +
                "    bucket1\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String bucket = args[0];

        //Create the S3Client object
        Region region = Region.US_WEST_2;
        s3 = S3Client.builder().region(region).build();

        listAllObjects(s3,bucket) ;
    }
    
    public static void listAllObjects(S3Client s3, String bucket) {

        try {
            // snippet-start:[s3.java2.s3_bucket_ops.delete_bucket]
            // To delete a bucket, all the objects in the bucket must be deleted first
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket).build();
            ListObjectsV2Response listObjectsV2Response;

            do {
                listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
                for (S3Object s3Object : listObjectsV2Response.contents()) {
                    s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(s3Object.key()).build());
                }

                listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket)
                        .continuationToken(listObjectsV2Response.nextContinuationToken())
                        .build();

            } while(listObjectsV2Response.isTruncated());
            // snippet-end:[s3.java2.s3_bucket_ops.delete_bucket]

            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucket).build();
            s3.deleteBucket(deleteBucketRequest);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[s3.java2.bucket_deletion.main]
