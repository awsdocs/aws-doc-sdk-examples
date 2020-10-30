//snippet-sourcedescription:[CreateAccessPoint.java demonstrates how to create an access point for an Amazon Simple Storage Service (S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/28/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.s3;

// snippet-start:[s3.java2.create_access_point.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateAccessPointRequest;
import software.amazon.awssdk.services.s3control.model.S3ControlException;
// snippet-end:[s3.java2.create_access_point.import]

public class CreateAccessPoint {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateAccessPoint <accountId><bucketName><accessPointName>\n\n" +
                "Where:\n" +
                "    accountId - the account id that owns the Amazon S3 bucket \n\n" +
                "    bucketName - the bucket name \n" +
                "    accessPointName - the access point name \n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Read the command line arguments
        String accountId = args[0];
        String bucketName = args[1];
        String accessPointName = args[2];

        // Create the S3ControlClient object
        Region region = Region.US_EAST_1;
        S3ControlClient s3ControlClient = S3ControlClient.builder()
                .region(region)
                .build();

        createSpecificAccessPoint(s3ControlClient, accountId, bucketName, accessPointName );
    }

    // snippet-start:[s3.java2.create_access_point.main]
    // This method creates an access point for the given Amazon S3 bucket
    public static void createSpecificAccessPoint(S3ControlClient s3ControlClient,
                                                 String accountId,
                                                 String bucketName,
                                                 String accessPointName ) {

        try {
            CreateAccessPointRequest accessPointRequest = CreateAccessPointRequest.builder()
                .accountId(accountId)
                .bucket(bucketName)
                .name(accessPointName)
                .build();

            s3ControlClient.createAccessPoint(accessPointRequest);
            System.out.println("The access point was created" );

        } catch (S3ControlException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[s3.java2.create_access_point.main]
}
