//snippet-sourcedescription:[ObjectCopy.java demonstrates how to copy an object between Amazon Simple Storage Service (Amazon S3) buckets using the Amazon S3 TransferManager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3.transfermanager;

// snippet-start:[s3.tm.java2.objectcopy.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedCopy;
import software.amazon.awssdk.transfer.s3.model.Copy;
import software.amazon.awssdk.transfer.s3.model.CopyRequest;

import java.util.UUID;
// snippet-end:[s3.tm.java2.objectcopy.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ObjectCopy {
    private static final Logger logger = LoggerFactory.getLogger(ObjectCopy.class);
    public final String bucketName = "x-" + UUID.randomUUID();
    public final String key = UUID.randomUUID().toString();
    public final String destinationBucket = "y-" + UUID.randomUUID();
    public final String destinationKey = UUID.randomUUID().toString();

    public ObjectCopy() {
        setUp();
    }

    public static void main(String[] args) {
        ObjectCopy copy = new ObjectCopy();

        String etag = copy.copyObject(S3ClientFactory.transferManager,copy.bucketName,
            copy.key, copy.destinationBucket, copy.destinationKey);
        logger.info("etag [{}]", etag);
        copy.cleanUp();
    }

    // snippet-start:[s3.tm.java2.objectcopy.main]
    public String copyObject(S3TransferManager transferManager, String bucketName,
                             String key, String destinationBucket, String destinationKey){
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
            .sourceBucket(bucketName)
            .sourceKey(key)
            .destinationBucket(destinationBucket)
            .destinationKey(destinationKey)
            .build();

        CopyRequest copyRequest = CopyRequest.builder()
            .copyObjectRequest(copyObjectRequest)
            .build();

        Copy copy = transferManager.copy(copyRequest);

        CompletedCopy completedCopy = copy.completionFuture().join();
        return completedCopy.response().copyObjectResult().eTag();
    }
    // snippet-end:[s3.tm.java2.objectcopy.main]

    private void setUp() {
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        S3ClientFactory.s3Client.putObject(builder -> builder
            .bucket(bucketName)
            .key(key), RequestBody.fromString("Hello World"));
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(destinationBucket));
    }

    public void cleanUp(){
        S3ClientFactory.s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
        S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(bucketName));
        S3ClientFactory.s3Client.deleteObject(b -> b.bucket(destinationBucket).key(destinationKey));
        S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(destinationBucket));
    }
}
