//snippet-sourcedescription:[S3AsyncStreamOps.java demonstrates how to use the streaming operations of an S3 asynchronous client]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2020-02-06]
//snippet-sourceauthor:[soo-aws]

/*
 * Copyright 2011-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
                "    objectname - the name pf the object object (i.e., book.pdf)\n" +
                "    path - the local path to the file (i.e., C:\\AWS\\book.pdf)\n" +
                "Example:\n" +
                "    bucket1 book.pdf  C:\\AWS\\book.pdf\n";

         // snippet-start:[s3.java2.copy_object.main]
         String bucketName = args[0];
         String objectKey = args[1];
         String path = args[2];

        S3AsyncClient client = S3AsyncClient.create();
        final CompletableFuture<GetObjectResponse> futureGet = client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build(),
                AsyncResponseTransformer.toFile(Paths.get(path)));
        futureGet.whenComplete((resp, err) -> {
            try {
                if (resp != null) {
                    System.out.println(resp);
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
