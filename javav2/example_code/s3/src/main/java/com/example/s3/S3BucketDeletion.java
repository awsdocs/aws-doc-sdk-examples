/*
 * Copyright 2011-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.Random;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.sync.RequestBody;

public class S3BucketDeletion {

    private static S3Client s3;

    public static void main(String[] args) throws Exception {
        Region region = Region.US_WEST_2;
        s3 = S3Client.builder().region(region).build();

        String bucket = "bucket" + System.currentTimeMillis();

        createBucket(bucket, region);
        // Delete empty bucket
        deleteNonEmptyBucket(bucket);


        String bucket2 = "bucket" + System.currentTimeMillis();
        createBucket(bucket2, region);
        putObjects(bucket2);

        // Delete non-empty bucket
        // To delete a bucket, all the objects in the bucket should be deleted first
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket2).build();
        ListObjectsV2Response listObjectsV2Response;
        do {
            listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
            for (S3Object s3Object : listObjectsV2Response.contents()) {
                s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket2).key(s3Object.key()).build());
            }

            listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket2)
                                                       .continuationToken(listObjectsV2Response.nextContinuationToken())
                                                       .build();

        } while (listObjectsV2Response.isTruncated());

        // Now the bucket is empty and we can delete it
        deleteNonEmptyBucket(bucket2);
    }

    private static void createBucket(String bucket, Region region) {
        // Create bucket
        s3.createBucket(CreateBucketRequest
                                .builder()
                                .bucket(bucket)
                                .createBucketConfiguration(
                                        CreateBucketConfiguration.builder()
                                                                 .locationConstraint(region.value())
                                                                 .build())
                                .build());
    }

    private static void putObjects(String bucket) {
        for (int i = 0; i < 5; i++) {
            try {
                s3.putObject(PutObjectRequest.builder().bucket(bucket).key("key" + i).build(),
                             RequestBody.of(getRandomByteBuffer(10_000)));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static void deleteNonEmptyBucket(String bucket) {
        // Delete empty bucket
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucket).build();
        s3.deleteBucket(deleteBucketRequest);
    }

    private static ByteBuffer getRandomByteBuffer(int size) throws IOException {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }
}
