//snippet-sourcedescription:[CreateSnapshot.java demonstrates how to create a copy of a cluster at a specific moment in time.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon MemoryDB for Redis]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.memorydb;

//snippet-start:[memoryDB.java2.create_snapshot.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import software.amazon.awssdk.services.memorydb.model.CreateSnapshotRequest;
import software.amazon.awssdk.services.memorydb.model.CreateSnapshotResponse;
import software.amazon.awssdk.services.memorydb.model.MemoryDbException;
//snippet-end:[memoryDB.java2.create_snapshot.import]

public class CreateSnapshot {

    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage:\n" +
            "    <clusterName> <snapShotName> \n\n" +
            "Where:\n" +
            "    clusterName - The name of the cluster. \n" +
            "    snapShotName - The name for the snapshot being created. \n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String clusterName = args[0];
        String snapShotName = args[1];
        Region region = Region.US_EAST_1;
        MemoryDbClient memoryDbClient = MemoryDbClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createSpecificSnapshot(memoryDbClient, clusterName, snapShotName);
    }

    //snippet-start:[memoryDB.java2.create_snapshot.main]
    public static void createSpecificSnapshot( MemoryDbClient memoryDbClient, String clusterName, String snapShotName) {

        try {
            CreateSnapshotRequest request = CreateSnapshotRequest.builder()
                .clusterName(clusterName)
                .snapshotName(snapShotName)
                .build();

            CreateSnapshotResponse response = memoryDbClient.createSnapshot(request);
            System.out.println("The ARN of the response is" +response.snapshot().arn());

        } catch (MemoryDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[memoryDB.java2.create_snapshot.main]
}
