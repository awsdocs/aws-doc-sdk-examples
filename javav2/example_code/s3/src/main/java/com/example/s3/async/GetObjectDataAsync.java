// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetObjectDataAsync.java demonstrates how to read data from an Amazon Simple Storage Service (Amazon S3) object using the Async client.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.s3.async;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GetObjectDataAsync {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <bucketName> <keyName> <path>\n\n" +
                "Where:\n" +
                "    bucketName - The Amazon S3 bucket name. \n\n"+
                "    keyName - The key name. \n\n"+
                "    path - The path where the file is written to. \n\n";

        if (args.length != 3) {
           System.out.println(usage);
            System.exit(1);
       }

        String bucketName = args[0];
        String keyName = args[1];
        String path = args[2];

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        getObject (s3AsyncClient, bucketName, keyName, path);
        s3AsyncClient.close();

    }
    public static void getObject (S3AsyncClient s3AsyncClient, String bucketName, String keyName, String path ) {

        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
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

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
