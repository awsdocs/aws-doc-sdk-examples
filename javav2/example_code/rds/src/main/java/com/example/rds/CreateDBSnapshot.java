//snippet-sourcedescription:[CreateDBSnapshot.java demonstrates how to create an Amazon Relational Database Service (RDS) snapshot.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Relational Database Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rds;

// snippet-start:[rds.java2.create_instance.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.CreateDbSnapshotRequest;
import software.amazon.awssdk.services.rds.model.CreateDbSnapshotResponse;
import software.amazon.awssdk.services.rds.model.RdsException;
// snippet-end:[rds.java2.create_instance.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateDBSnapshot {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <dbInstanceIdentifier> <dbSnapshotIdentifier> \n\n" +
                "Where:\n" +
                "    dbInstanceIdentifier - The database instance identifier. \n" +
                "    dbSnapshotIdentifier - The snapshot identifier. \n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String dbInstanceIdentifier = args[0];
        String dbSnapshotIdentifier = args[1];

        Region region = Region.US_WEST_2;
        RdsClient rdsClient = RdsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        createSnapshot(rdsClient, dbInstanceIdentifier, dbSnapshotIdentifier) ;
        rdsClient.close();
    }

    // snippet-start:[rds.java2.create_instance.main]
    public static void createSnapshot(RdsClient rdsClient, String dbInstanceIdentifier, String dbSnapshotIdentifier) {

        try {
            CreateDbSnapshotRequest snapshotRequest = CreateDbSnapshotRequest.builder()
                    .dbInstanceIdentifier(dbInstanceIdentifier)
                    .dbSnapshotIdentifier(dbSnapshotIdentifier)
                    .build();

            CreateDbSnapshotResponse response = rdsClient.createDBSnapshot(snapshotRequest);
            System.out.print("The Snapshot id is " + response.dbSnapshot().dbiResourceId());

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.create_instance.main]
}
