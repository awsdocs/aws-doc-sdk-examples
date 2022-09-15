//snippet-sourcedescription:[DescribeClusters.java demonstrates how to retrieve information about all provisioned clusters.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon MemoryDB for Redis]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.memorydb;

//snippet-start:[memoryDB.java2.describe_clusters.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import software.amazon.awssdk.services.memorydb.model.Cluster;
import software.amazon.awssdk.services.memorydb.model.DescribeClustersRequest;
import software.amazon.awssdk.services.memorydb.model.DescribeClustersResponse;
import software.amazon.awssdk.services.memorydb.model.MemoryDbException;
import java.util.List;
//snippet-end:[memoryDB.java2.describe_clusters.import]

public class DescribeClusters {

    public static void main(String[] args) {
        Region region = Region.US_EAST_1;
        MemoryDbClient memoryDbClient = MemoryDbClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        getClusters(memoryDbClient);
    }

    //snippet-start:[memoryDB.java2.describe_clusters.main]
    public static void getClusters(MemoryDbClient memoryDbClient) {

        try {
            DescribeClustersRequest request = DescribeClustersRequest.builder()
                .build();

            DescribeClustersResponse response = memoryDbClient.describeClusters(request);
            List<Cluster> clusters = response.clusters();
            for (Cluster cluster: clusters) {
                System.out.println("The cluster name is: "+cluster.name());
                System.out.println("The cluster ARN is: "+cluster.arn());
                System.out.println("Endpoint is: "+cluster.clusterEndpoint());
            }

        } catch (MemoryDbException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[memoryDB.java2.describe_clusters.main]
}
