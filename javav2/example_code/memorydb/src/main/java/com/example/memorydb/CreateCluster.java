//snippet-sourcedescription:[CreateCluster.java demonstrates how to create a cluster.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon MemoryDB for Redis]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.memorydb;

//snippet-start:[memoryDB.java2.create_cluster.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import software.amazon.awssdk.services.memorydb.model.CreateClusterRequest;
import software.amazon.awssdk.services.memorydb.model.CreateClusterResponse;
import software.amazon.awssdk.services.memorydb.model.MemoryDbException;
//snippet-end:[memoryDB.java2.create_cluster.import]

public class CreateCluster {

    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage:\n" +
            "    <clusterName> <nodeType> <subnetGroupName> <aclName> \n\n" +
            "Where:\n" +
            "    clusterName - The name of the cluster. \n" +
            "    nodeType - The compute and memory capacity of the nodes in the cluster. \n" +
            "    subnetGroupName - The name of the subnet group to use for the cluster. \n" +
            "    aclName - The name of the access control list (ACL) to associate with the cluster. \n" ;

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String clusterName = args[0];
        String nodeType = args[1];
        String subnetGroupName = args[2];
        String aclName = args[3];
        Region region = Region.US_EAST_1;
        MemoryDbClient memoryDbClient = MemoryDbClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createSingleCluster(memoryDbClient, clusterName, nodeType, subnetGroupName, aclName);
    }

    //snippet-start:[memoryDB.java2.create_cluster.import]
    public static void createSingleCluster(MemoryDbClient memoryDbClient, String clusterName, String nodeType, String subnetGroupName, String aclName) {

        try{
            CreateClusterRequest request = CreateClusterRequest.builder()
                .clusterName(clusterName)
                .aclName(aclName)
                .description("Created using the AWS SDK for Java")
                .numShards(1)
                .nodeType(nodeType)
                .port(6379)
                .subnetGroupName(subnetGroupName)
                .build() ;

            CreateClusterResponse response = memoryDbClient.createCluster(request);
            System.out.println("Cluster created. The ARN is "+response.cluster().arn());

        } catch (MemoryDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[memoryDB.java2.create_cluster.import]
}
