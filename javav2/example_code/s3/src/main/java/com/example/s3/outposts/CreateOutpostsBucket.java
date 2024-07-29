// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.outposts;

// snippet-start:[s3-outposts.java2.create_bucket.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3control.model.CreateBucketResponse;
// snippet-end:[s3-outposts.java2.create_bucket.import]

public class CreateOutpostsBucket {
    private static final Logger logger = LoggerFactory.getLogger(CreateOutpostsBucket.class);

    public static void main(String[] args) {
        createOutpostsBucket();
    }

    // snippet-start:[s3-outposts.java2.create_bucket]
    public static void createOutpostsBucket() {
        try (S3ControlClient s3ControlClient = S3ControlClient.create()) {
            try {
                CreateBucketResponse response = s3ControlClient.createBucket(b -> b
                        .bucket("<bucket-name>")
                        .outpostId("op-<123456789abcdefgh>")
                        .createBucketConfiguration(CreateBucketConfiguration.builder().build()));
                logger.info("Bucket created with Arn: [{}]", response.bucketArn());
            } catch (SdkException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    // snippet-end:[s3-outposts.java2.create_bucket]
}

