//snippet-sourcedescription:[S3BucketDeletion.java demonstrates how to delete an empty Amazon Simple Storage Service (Amazon S3) bucket.]
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

// snippet-start:[s3.java2.bucket_deletion.import]
// snippet-start:[s3.java2.s3_bucket_ops.delete_bucket.import]
import software.amazon.awssdk.regions.Region;
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

/**
 * To run this AWS code example, ensure that you have setup your development environment, including your AWS credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class S3BucketDeletion {

    public static void main(String[] args) throws Exception {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    <bucket>\n\n" +
                "Where:\n" +
                "    bucket - the bucket to delete (for example, bucket1). \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucket = args[0];
        // snippet-start:[s3.java2.bucket_deletion.region]
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();
        // snippet-start:[s3.java2.bucket_deletion.region]

        deleteAllObjects(s3,bucket) ;
        s3.close();
    }

    // snippet-start:[s3.java2.bucket_deletion.delete_all_objects_bucket]
    public static void deleteAllObjects(S3Client s3, String bucket) {

        try {
            // To delete a bucket, all the objects in the bucket must be deleted first
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket).build();
            ListObjectsV2Response listObjectsV2Response;

            do {
                listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
                for (S3Object s3Object : listObjectsV2Response.contents()) {
                    s3.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3Object.key())
                            .build());
                }

                listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket)
                        .continuationToken(listObjectsV2Response.nextContinuationToken())
                        .build();

            } while(listObjectsV2Response.isTruncated());
            // snippet-end:[s3.java2.bucket_deletion.delete_all_objects_bucket]

            // snippet-start:[s3.java2.bucket_deletion.delete_bucket]
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucket).build();
            s3.deleteBucket(deleteBucketRequest);
            // snippet-end:[s3.java2.bucket_deletion.delete_bucket]

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[s3.java2.bucket_deletion.main]
}

