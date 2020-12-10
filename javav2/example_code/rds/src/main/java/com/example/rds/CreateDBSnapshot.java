//snippet-sourcedescription:[CreateDBSnapshot.java demonstrates how to create an Amazon Relational Database Service (RDS) snapshot.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Relational Database Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/6/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rds;

// snippet-start:[rds.java2.create_instance.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.CreateDbSnapshotRequest;
import software.amazon.awssdk.services.rds.model.CreateDbSnapshotResponse;
import software.amazon.awssdk.services.rds.model.RdsException;
// snippet-end:[rds.java2.create_instance.import]

public class CreateDBSnapshot {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateDBSnapshot <dbInstanceIdentifier> <dbSnapshotIdentifier> \n\n" +
                "Where:\n" +
                "    dbInstanceIdentifier - the database instance identifier \n" +
                "    dbSnapshotIdentifier - the snapshot identifier \n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String dbInstanceIdentifier = args[0];
        String dbSnapshotIdentifier = args[1];

        Region region = Region.US_WEST_2;
        RdsClient rdsClient = RdsClient.builder()
                .region(region)
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
        // snippet-end:[rds.java2.create_instance.main]
    }
}
