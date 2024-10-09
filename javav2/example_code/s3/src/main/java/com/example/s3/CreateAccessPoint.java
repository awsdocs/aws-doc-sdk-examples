// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

// snippet-start:[s3.java2.create_access_point.main]
// snippet-start:[s3.java2.create_access_point.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateAccessPointRequest;
import software.amazon.awssdk.services.s3control.model.S3ControlException;
import software.amazon.awssdk.services.s3control.model.DeleteAccessPointRequest;
// snippet-end:[s3.java2.create_access_point.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateAccessPoint {
    public static void main(String[] args) {
        final String usage = """

            Usage:
                <accountId> <bucketName> <accessPointName>

            Where:
                accountId - The account id that owns the Amazon S3 bucket.\s
                bucketName - The Amazon S3 bucket name.\s
                accessPointName - The access point name (for example, myaccesspoint).\s
            """;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String accountId = args[0];
        String bucketName = args[1];
        String accessPointName = args[2];
        Region region = Region.US_EAST_1;
        S3ControlClient s3ControlClient = S3ControlClient.builder()
            .region(region)
            .build();

        createSpecificAccessPoint(s3ControlClient, accountId, bucketName, accessPointName);
        deleteSpecificAccessPoint(s3ControlClient, accountId, accessPointName);
        s3ControlClient.close();
    }

    /**
     * Creates a specific access point on an Amazon S3 bucket.
     *
     * @param s3ControlClient the S3 Control client to use for the operation
     * @param accountId the AWS account ID associated with the bucket
     * @param bucketName the name of the S3 bucket
     * @param accessPointName the name of the access point to be created
     *
     * @throws S3ControlException if there is an error creating the access point
     */
    public static void createSpecificAccessPoint(S3ControlClient s3ControlClient, String accountId, String bucketName,
                                                 String accessPointName) {
        try {
            CreateAccessPointRequest accessPointRequest = CreateAccessPointRequest.builder()
                .accountId(accountId)
                .bucket(bucketName)
                .name(accessPointName)
                .build();

            s3ControlClient.createAccessPoint(accessPointRequest);
            System.out.println("The access point was created");

        } catch (S3ControlException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Deletes a specific S3 access point.
     *
     * @param s3ControlClient the S3 Control client to use for the operation
     * @param accountId the account ID of the access point to delete
     * @param accessPointName the name of the access point to delete
     * @throws S3ControlException if an error occurs while deleting the access point
     */
    public static void deleteSpecificAccessPoint(S3ControlClient s3ControlClient, String accountId,
                                                 String accessPointName) {
        try {
            DeleteAccessPointRequest deleteAccessPointRequest = DeleteAccessPointRequest.builder()
                .name(accessPointName)
                .accountId(accountId)
                .build();

            s3ControlClient.deleteAccessPoint(deleteAccessPointRequest);
            System.out.println("The access point was deleted");

        } catch (S3ControlException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[s3.java2.create_access_point.main]
