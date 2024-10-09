// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.async;
// snippet-start:[s3.java2.async_stream_ops.complete]

// snippet-start:[s3.java2.async_stream_ops.import]
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
// snippet-end:[s3.java2.async_stream_ops.import]

// snippet-start:[s3.java2.async_stream_ops.main]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class S3AsyncStreamOps {
    public static void main(String[] args) {
        final String usage = """
                Usage:
                    <bucketName> <objectKey> <path>

                Where:
                    bucketName - The name of the Amazon S3 bucket (for example, bucket1).\s
                    objectKey - The name of the object (for example, book.pdf).\s
                    path - The local path to the file (for example, C:/AWS/book.pdf).\s
                """;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String bucketName = args[0];
        String objectKey = args[1];
        String path = args[2];
        Region region = Region.US_EAST_1;
        S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                .region(region)
                .build();

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        CompletableFuture<GetObjectResponse> futureGet = s3AsyncClient.getObject(objectRequest,
                AsyncResponseTransformer.toFile(Paths.get(path)));

        futureGet.whenComplete((resp, err) -> {
            try {
                if (resp != null) {
                    System.out.println("Object downloaded. Details: " + resp);
                } else {
                    err.printStackTrace();
                }
            } finally {
                // Only close the client when you are completely done with it.
                s3AsyncClient.close();
            }
        });
        futureGet.join();
    }
}
// snippet-end:[s3.java2.async_stream_ops.main]
// snippet-end:[s3.java2.async_stream_ops.complete]