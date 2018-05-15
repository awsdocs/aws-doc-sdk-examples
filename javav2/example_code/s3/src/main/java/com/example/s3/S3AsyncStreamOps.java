/*
 * Copyright 2011-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.utils.FunctionalUtils;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class S3AsyncStreamOps {

    private static final String BUCKET = "sample-bucket";
	private static final String KEY = "testfile.out";

	public static void main(String[] args) {
    	S3AsyncClient client = S3AsyncClient.create();
      final CompletableFuture<GetObjectResponse> futureGet = client.getObject(
                GetObjectRequest.builder()
                                .bucket(BUCKET)
                                .key(KEY)
                                .build(),
                AsyncResponseTransformer.toFile(Paths.get("myfile.out")));
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
                FunctionalUtils.invokeSafely(client::close);
            }
        });
    }
}
