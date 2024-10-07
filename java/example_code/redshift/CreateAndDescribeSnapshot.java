// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[redshift.java.CreateAndDescribeSnapshot.complete]

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.services.redshift.model.*;

public class CreateAndDescribeSnapshot {

    public static AmazonRedshift client;
    public static String clusterIdentifier = "***provide a cluster identifier***";
    public static long sleepTime = 20;

    
    public static void main(String[] args) throws IOException {

        // Default client using the {@link
        // com.amazonaws.auth.DefaultAWSCredentialsProviderChain}
        client = AmazonRedshiftClientBuilder.defaultClient();
        try {
            // Unique snapshot identifier
            String snapshotId = "my-snapshot-" + (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(new Date());

            Date createDate = createManualSnapshot(snapshotId);
            waitForSnapshotAvailable(snapshotId);
            describeSnapshots();
            deleteManualSnapshotsBefore(createDate);
            describeSnapshots();

        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    private static Date createManualSnapshot(String snapshotId) {

        CreateClusterSnapshotRequest request = new CreateClusterSnapshotRequest()
                .withClusterIdentifier(clusterIdentifier)
                .withSnapshotIdentifier(snapshotId);
        Snapshot snapshot = client.createClusterSnapshot(request);
        System.out.format("Created cluster snapshot: %s\n", snapshotId);
        return snapshot.getSnapshotCreateTime();
    }

    private static void describeSnapshots() {

        DescribeClusterSnapshotsRequest request = new DescribeClusterSnapshotsRequest()
                .withClusterIdentifier(clusterIdentifier);
        DescribeClusterSnapshotsResult result = client.describeClusterSnapshots(request);

        printResultSnapshots(result);
    }

    private static void deleteManualSnapshotsBefore(Date creationDate) {

        DescribeClusterSnapshotsRequest request = new DescribeClusterSnapshotsRequest()
                .withEndTime(creationDate)
                .withClusterIdentifier(clusterIdentifier)
                .withSnapshotType("manual");

        DescribeClusterSnapshotsResult result = client.describeClusterSnapshots(request);

        for (Snapshot s : result.getSnapshots()) {
            DeleteClusterSnapshotRequest deleteRequest = new DeleteClusterSnapshotRequest()
                    .withSnapshotIdentifier(s.getSnapshotIdentifier());
            Snapshot deleteResult = client.deleteClusterSnapshot(deleteRequest);
            System.out.format("Deleted snapshot %s\n", deleteResult.getSnapshotIdentifier());
        }
    }

    private static void printResultSnapshots(DescribeClusterSnapshotsResult result) {
        System.out.println("\nSnapshot listing:");
        for (Snapshot snapshot : result.getSnapshots()) {
            System.out.format("Identifier: %s\n", snapshot.getSnapshotIdentifier());
            System.out.format("Snapshot type: %s\n", snapshot.getSnapshotType());
            System.out.format("Snapshot create time: %s\n", snapshot.getSnapshotCreateTime());
            System.out.format("Snapshot status: %s\n\n", snapshot.getStatus());
        }
    }

    private static Boolean waitForSnapshotAvailable(String snapshotId) throws InterruptedException {
        Boolean snapshotAvailable = false;
        System.out.println("Waiting for snapshot to become available.");
        while (!snapshotAvailable) {
            DescribeClusterSnapshotsResult result = client
                    .describeClusterSnapshots(new DescribeClusterSnapshotsRequest()
                            .withSnapshotIdentifier(snapshotId));
            String status = (result.getSnapshots()).get(0).getStatus();
            if (status.equalsIgnoreCase("available")) {
                snapshotAvailable = true;
            } else {
                System.out.print(".");
                Thread.sleep(sleepTime * 1000);
            }
        }
        return snapshotAvailable;
    }

}
// snippet-end:[redshift.java.CreateAndDescribeSnapshot.complete]
