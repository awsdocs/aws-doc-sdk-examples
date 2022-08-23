//snippet-sourcedescription:[DeleteClusters.java demonstrates how to delete a cluster.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon MemoryDB for Redis]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.memorydb;

//snippet-start:[memoryDB.java2.del_cluster.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import software.amazon.awssdk.services.memorydb.model.DeleteClusterRequest;
import software.amazon.awssdk.services.memorydb.model.MemoryDbException;
//snippet-end:[memoryDB.java2.del_cluster.import]

public class DeleteCluster {

    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage:\n" +
            "    <clusterName>  \n\n" +
            "Where:\n" +
            "    clusterName - The name of the cluster. \n";

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

        deleteSpecificCluster(memoryDbClient, clusterName);
    }

    //snippet-start:[memoryDB.java2.del_cluster.main]
    public static void deleteSpecificCluster(MemoryDbClient memoryDbClient, String clusterName) {

        try {
            DeleteClusterRequest clusterRequest = DeleteClusterRequest.builder()
                .clusterName(clusterName)
                .build();

            memoryDbClient.deleteCluster(clusterRequest);
            System.out.println(clusterName +" was successfully deleted");

        } catch (MemoryDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[memoryDB.java2.del_cluster.main]
}
