//snippet-sourcedescription:[CreateAccessPoint.java demonstrates how to create and delete an access point for an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.s3;

// snippet-start:[s3.java2.create_access_point.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateAccessPointRequest;
import software.amazon.awssdk.services.s3control.model.S3ControlException;
import software.amazon.awssdk.services.s3control.model.DeleteAccessPointRequest;
// snippet-end:[s3.java2.create_access_point.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateAccessPoint {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <accountId> <bucketName> <accessPointName>\n\n" +
            "Where:\n" +
            "    accountId - The account id that owns the Amazon S3 bucket. \n\n" +
            "    bucketName - The Amazon S3 bucket name. \n" +
            "    accessPointName - The access point name (for example, myaccesspoint). \n";

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String accountId = args[0];
        String bucketName = args[1];
        String accessPointName = args[2];

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        S3ControlClient s3ControlClient = S3ControlClient.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .build();

        createSpecificAccessPoint(s3ControlClient, accountId, bucketName, accessPointName );
        deleteSpecificAccessPoint(s3ControlClient, accountId, accessPointName);
        s3ControlClient.close();
    }

    // snippet-start:[s3.java2.create_access_point.main]
    // This method creates an access point for the given Amazon S3 bucket.
    public static void createSpecificAccessPoint(S3ControlClient s3ControlClient, String accountId, String bucketName, String accessPointName) {

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
    
    public static void deleteSpecificAccessPoint(S3ControlClient s3ControlClient, String accountId, String accessPointName) {
        
        try {
            DeleteAccessPointRequest deleteAccessPointRequest = DeleteAccessPointRequest.builder()
                .name(accessPointName)
                .accountId(accountId)
                .build();

            s3ControlClient.deleteAccessPoint(deleteAccessPointRequest);
            System.out.println("The access point was deleted" );

        } catch (S3ControlException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
