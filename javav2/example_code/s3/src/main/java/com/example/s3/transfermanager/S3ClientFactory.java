//snippet-sourcedescription:[S3ClientFactory.java demonstrates how to create instances of the Amazon Simple Storage Service (Amazon S3) TransferManager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3.transfermanager;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class S3ClientFactory {
    public static final S3TransferManager transferManager = createCustonTm();
    public static final S3TransferManager transferManagerWithDefaults = createDefaultTm();
    public static final S3Client s3Client;

    private static S3TransferManager createCustonTm(){
        // snippet-start:[s3.tm.java2.s3clientfactory.create_custom_tm]
        S3AsyncClient s3AsyncClient =
            S3AsyncClient.crtBuilder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .targetThroughputInGbps(20.0)
                .minimumPartSizeInBytes(8 * MB)
                .build();

        S3TransferManager transferManager =
            S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();
        // snippet-end:[s3.tm.java2.s3clientfactory.create_custom_tm]
        return transferManager;
    }

    private static S3TransferManager createDefaultTm(){
        // snippet-start:[s3.tm.java2.s3clientfactory.create_default_tm]
        S3TransferManager transferManager = S3TransferManager.create();
        // snippet-end:[s3.tm.java2.s3clientfactory.create_default_tm]
        return transferManager;
    }

    static {
        s3Client = S3Client.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.US_EAST_1)
            .build();
    }
}
