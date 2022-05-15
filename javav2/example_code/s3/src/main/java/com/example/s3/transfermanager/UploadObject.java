//snippet-sourcedescription:[UploadObject.java demonstrates how to upload an object to an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[01/05/2022]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.transfermanager;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.transfer.s3.FileUpload;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import java.nio.file.Paths;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UploadObject {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "  <bucketName> <objectKey> <objectPath> \n\n" +
                "Where:\n" +
                "  bucketName - the Amazon S3 bucket to upload an object into.\n" +
                "  objectKey - the object to upload (for example, book.pdf).\n" +
                "  objectPath - the path where the file is located (for example, C:/AWS/book2.pdf). \n\n" ;

       if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
       }

        long MB = 1024;
        String bucketName = args[0];
        String objectKey = args[1];
        String objectPath = args[2];


        System.out.println("Putting an object into bucket "+bucketName +" using the S3TransferManager");
        Region region = Region.US_EAST_1;
        S3TransferManager transferManager =  S3TransferManager.builder()
                .s3ClientConfiguration(cfg ->cfg.region(region)
                        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                        .targetThroughputInGbps(20.0)
                        .minimumPartSizeInBytes(10 * MB))
                .build();

        uploadObjectTM(transferManager, bucketName,  objectKey, objectPath);
        System.out.println("Object was successfully uploaded using the Transfer Manager.");
        transferManager.close();
    }

    public static void uploadObjectTM( S3TransferManager transferManager,  String bucketName,  String objectKey, String objectPath) {

        FileUpload upload =
                transferManager.uploadFile(u -> u.source(Paths.get(objectPath))
                        .putObjectRequest(p -> p.bucket(bucketName).key(objectKey)));
        upload.completionFuture().join();
    }
}
