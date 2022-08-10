//snippet-sourcedescription:[DescribeSpecificCluster.java demonstrates how to retrieve information about a specific clusters]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon MemoryDB for Redis]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.memorydb;

//snippet-start:[memoryDB.java2.describe_sin_cluster.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import software.amazon.awssdk.services.memorydb.model.Cluster;
import software.amazon.awssdk.services.memorydb.model.DescribeClustersRequest;
import software.amazon.awssdk.services.memorydb.model.DescribeClustersResponse;
import software.amazon.awssdk.services.memorydb.model.MemoryDbException;
import java.util.List;
//snippet-end:[memoryDB.java2.describe_sin_cluster.import]

public class DescribeSpecificCluster {

    public static void main(String[] args) {

        final String usage = "\n" +
             "Usage:\n" +
             "    <clusterName> \n\n" +
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

        checkIfAvailable(memoryDbClient, clusterName);
    }

    //snippet-start:[memoryDB.java2.describe_sin_cluster.main]
    public static void checkIfAvailable(MemoryDbClient memoryDbClient, String clusterName) {

        try {
            // Break when cluster is Available
            String status;
            Cluster myCluster = null;
            boolean clusterAvailable = false;
            while (!clusterAvailable) {
                // Get new Cluster object.
                myCluster = getCluster(memoryDbClient, clusterName);
                status = myCluster.status();
                if ((status.compareTo("creating") == 0) || (status.compareTo("snapshotting") == 0)) {
                    Thread.sleep(2000);
                    System.out.println("The status is " + status);
                } else {
                    clusterAvailable = true;
                }
            }
            System.out.println("The " +myCluster.name() +"state is "+myCluster.status() );

        } catch (MemoryDbException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // Returns a Cluster object.
    private static Cluster getCluster(MemoryDbClient memoryDbClient, String clusterName) {

        try {
            DescribeClustersRequest request = DescribeClustersRequest.builder()
                .clusterName(clusterName)
                .build();

            DescribeClustersResponse response = memoryDbClient.describeClusters(request);
            List<Cluster> clusters = response.clusters();

            //Return the only cluster in list
            for (Cluster cluster: clusters) {
                return cluster;
            }

        } catch (MemoryDbException e ) {
            System.err.println(e.awsErrorDetails());
            System.exit(1);
        }
        return null ;
    }
    //snippet-end:[memoryDB.java2.describe_sin_cluster.main]
}
