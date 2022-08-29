//snippet-sourcedescription:[GetObject.java demonstrates how to download an object from an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.transfermanager;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.transfer.s3.FileDownload;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import java.nio.file.Paths;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GetObject {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "  <bucketName> <objectKey> <objectPath> \n\n" +
            "Where:\n" +
            "  bucketName - The Amazon S3 bucket to upload an object into.\n" +
            "  objectKey - The object to download (for example, book.pdf).\n" +
            "  objectPath - The path where the file is written (for example, C:/AWS/book2.pdf). \n\n" ;

       if (args.length != 3) {
              System.out.println(usage);
              System.exit(1);
        }

        long mb = 1024;
        String bucketName = args[0];
        String objectKey = args[1];
        String objectPath = args[2];

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        S3TransferManager transferManager = S3TransferManager.builder()
            .s3ClientConfiguration(cfg ->cfg.region(region)
            .credentialsProvider(credentialsProvider)
            .targetThroughputInGbps(20.0)
            .minimumPartSizeInBytes(10 * mb))
            .build();

        downloadObjectTM(transferManager, bucketName, objectKey, objectPath);
        System.out.println("Object was successfully downloaded using the Transfer Manager.");
        transferManager.close();
    }

    public static void downloadObjectTM(S3TransferManager transferManager, String bucketName, String objectKey, String objectPath) {
        FileDownload download =
                transferManager.downloadFile(d -> d.getObjectRequest(g -> g.bucket(bucketName).key(objectKey))
                        .destination(Paths.get(objectPath)));
        download.completionFuture().join();
    }
}
