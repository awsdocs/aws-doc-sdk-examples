// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.memorydb;

// snippet-start:[memoryDB.java2.create_snapshot.main]
// snippet-start:[memoryDB.java2.create_snapshot.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import software.amazon.awssdk.services.memorydb.model.CreateSnapshotRequest;
import software.amazon.awssdk.services.memorydb.model.CreateSnapshotResponse;
import software.amazon.awssdk.services.memorydb.model.MemoryDbException;
// snippet-end:[memoryDB.java2.create_snapshot.import]

public class CreateSnapshot {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <clusterName> <snapShotName>\s

                Where:
                    clusterName - The name of the cluster.\s
                    snapShotName - The name for the snapshot being created.\s
                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String clusterName = args[0];
        String snapShotName = args[1];
        Region region = Region.US_EAST_1;
        MemoryDbClient memoryDbClient = MemoryDbClient.builder()
                .region(region)
                .build();

        createSpecificSnapshot(memoryDbClient, clusterName, snapShotName);
    }

    public static void createSpecificSnapshot(MemoryDbClient memoryDbClient, String clusterName, String snapShotName) {
        try {
            CreateSnapshotRequest request = CreateSnapshotRequest.builder()
                    .clusterName(clusterName)
                    .snapshotName(snapShotName)
                    .build();

            CreateSnapshotResponse response = memoryDbClient.createSnapshot(request);
            System.out.println("The ARN of the response is" + response.snapshot().arn());

        } catch (MemoryDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[memoryDB.java2.create_snapshot.main]
