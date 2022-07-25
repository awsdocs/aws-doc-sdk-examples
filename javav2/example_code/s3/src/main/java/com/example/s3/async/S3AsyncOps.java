//snippet-sourcedescription:[S3AsyncOps.java demonstrates how to use the asynchronous client to place an object into an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.s3.async;
// snippet-start:[s3.java2.async_ops.complete]
// snippet-start:[s3.java2.async_ops.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
// snippet-end:[s3.java2.async_ops.import]
// snippet-start:[s3.java2.async_ops.main]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class S3AsyncOps {

     public static void main(String[] args) {

         final String usage = "\n" +
                 "Usage:\n" +
                 "    <bucketName> <key> <path>\n\n" +
                 "Where:\n" +
                 "    bucketName - The name of the Amazon S3 bucket (for example, bucket1). \n\n" +
                 "    key - The name of the object (for example, book.pdf). \n" +
                 "    path - The local path to the file (for example, C:/AWS/book.pdf). \n";

         if (args.length != 3) {
             System.out.println(usage);
             System.exit(1);
         }

         String bucketName = args[0];
         String key = args[1];
         String path = args[2];

         ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
         Region region = Region.US_EAST_1;
         S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                 .region(region)
                 .credentialsProvider(credentialsProvider)
                 .build();

         putObjectAsync(s3AsyncClient, bucketName, key, path);
     }

     public static void putObjectAsync(S3AsyncClient client,String bucketName, String key, String path) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        CompletableFuture<PutObjectResponse> future = client.putObject(objectRequest,
                AsyncRequestBody.fromFile(Paths.get(path))
        );
        future.whenComplete((resp, err) -> {
            try {
                if (resp != null) {
                    System.out.println("Object uploaded. Details: " + resp);
                } else {
                    // Handle error.
                    err.printStackTrace();
                }
            } finally {
                // Only close the client when you are completely done with it.
                client.close();
            }
        });

        future.join();
    }
}

// snippet-end:[s3.java2.async_ops.main]
// snippet-end:[s3.java2.async_ops.complete]