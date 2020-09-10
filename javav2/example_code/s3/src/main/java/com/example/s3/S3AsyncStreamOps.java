//snippet-sourcedescription:[S3AsyncStreamOps.java demonstrates how to use the streaming operations of an S3 asynchronous client]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
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
public class S3AsyncStreamOps {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    S3AsyncOps <bucketname> <objectname> <path>\n\n" +
                "Where:\n" +
                "    bucketname - the name of the bucket (i.e., bucket1)\n\n" +
                "    objectname - the name of the object (i.e., book.pdf)\n" +
                "    path - the local path where the file is written (i.e., C:\\AWS\\book.pdf)\n" +
                "Example:\n" +
                "    bucket1 book.pdf  C:\\AWS\\book.pdf\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
         }

        String bucketName = args[0];
        String objectKey =  args[1];
        String path = args[2];

        Region region = Region.US_WEST_2;
        S3AsyncClient client = S3AsyncClient.builder()
                .region(region)
                .build();

        // Create a GetObjectRequest instance
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        CompletableFuture<GetObjectResponse> futureGet = client.getObject(objectRequest,
                AsyncResponseTransformer.toFile(Paths.get(path)));

        futureGet.whenComplete((resp, err) -> {
            try {
                if (resp != null) {
                      System.out.println("Object downloaded. Details: " + resp);
                } else {
                    // Handle error
                    err.printStackTrace();
                }
            } finally {
                // Lets the application shut down. Only close the client when you are completely done with it
                client.close();
            }
        });
        futureGet.join();
    }
}

// snippet-end:[s3.java2.async_stream_ops.main]
// snippet-end:[s3.java2.async_stream_ops.complete]
