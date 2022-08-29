//snippet-sourcedescription:[DescribeSnapshots.java demonstrates how to return information about cluster snapshots.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon MemoryDB for Redis]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.memorydb;

//snippet-start:[memoryDB.java2.describe_snapshot.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import software.amazon.awssdk.services.memorydb.model.DescribeSnapshotsRequest;
import software.amazon.awssdk.services.memorydb.model.DescribeSnapshotsResponse;
import software.amazon.awssdk.services.memorydb.model.MemoryDbException;
import software.amazon.awssdk.services.memorydb.model.Snapshot;
import java.util.List;
//snippet-end:[memoryDB.java2.describe_snapshot.import]

public class DescribeSnapshots{

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <clusterName>  \n\n" +
            "Where:\n" +
            "    clusterName - The name of the cluster. \n" ;

        if (args.length != 1) {
           System.out.println(usage);
           System.exit(1);
        }

        String clusterName = args[0];
        Region region = Region.US_EAST_1;
        MemoryDbClient memoryDbClient = MemoryDbClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        describeALlSnapshots(memoryDbClient, clusterName);
    }

    //snippet-start:[memoryDB.java2.describe_snapshot.main]
    public static void describeALlSnapshots(MemoryDbClient memoryDbClient, String clusterName) {

        try {
            DescribeSnapshotsRequest request = DescribeSnapshotsRequest.builder()
                .maxResults(10)
                .clusterName(clusterName)
                .build();

            DescribeSnapshotsResponse response = memoryDbClient.describeSnapshots(request);
            List<Snapshot> snapshots = response.snapshots();
            for (Snapshot snapshot : snapshots) {
                System.out.println("The snapshot name is " + snapshot.name());
                System.out.println("The snapshot ARN is " + snapshot.arn());
            }

        } catch (MemoryDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[memoryDB.java2.describe_snapshot.main]
}
