//snippet-sourcedescription:[CreateAndModifyCluster.java demonstrates how to create and modify an Amazon Redshift cluster.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Redshift ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/6/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.redshift;

// snippet-start:[redshift.java2.create_cluster.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.CreateClusterRequest;
import software.amazon.awssdk.services.redshift.model.CreateClusterResponse;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
import software.amazon.awssdk.services.redshift.model.DescribeClustersRequest;
import software.amazon.awssdk.services.redshift.model.DescribeClustersResponse;
import software.amazon.awssdk.services.redshift.model.Cluster;
import software.amazon.awssdk.services.redshift.model.ModifyClusterResponse;
import software.amazon.awssdk.services.redshift.model.ModifyClusterRequest;
import java.util.List;
// snippet-end:[redshift.java2.create_cluster.import]

public class CreateAndModifyCluster {

    public static long sleepTime = 20;

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateAndModifyCluster <clusterId><masterUsername><masterUserPassword> \n\n" +
                "Where:\n" +
                "    clusterId - the id of the cluster to create \n" +
                "    masterUsername - the master user name \n" +
                "    masterUserPassword - the password that corresponds to the master user name \n" ;



        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String clusterId = args[0];
        String masterUsername = args[1];
        String masterUserPassword = args[2];

        Region region = Region.US_WEST_2;
        RedshiftClient redshiftClient = RedshiftClient.builder()
                .region(region)
                .build();

        createCluster(redshiftClient,clusterId, masterUsername, masterUserPassword );
        waitForClusterReady(redshiftClient, clusterId);
        modifyCluster(redshiftClient, clusterId);
    }

    // snippet-start:[redshift.java2.create_cluster.main]
    public static void createCluster(RedshiftClient redshiftClient, String clusterId, String masterUsername, String masterUserPassword ) {

       try {
        CreateClusterRequest clusterRequest = CreateClusterRequest.builder()
                .clusterIdentifier(clusterId)
                .masterUsername(masterUsername) // set the user name here
                .masterUserPassword(masterUserPassword) // set the user password here
                .nodeType("ds2.xlarge")
                .numberOfNodes(2)
                .build();

        CreateClusterResponse clusterResponse = redshiftClient.createCluster(clusterRequest);
        System.out.println("Created cluster " + clusterResponse.cluster().clusterIdentifier());

       } catch (RedshiftException e) {

           System.err.println(e.getMessage());
           System.exit(1);
       }
    }

    // Waits until the cluster is available
    public static void waitForClusterReady(RedshiftClient redshiftClient, String clusterId) {

        Boolean clusterReady = false;
        String clusterReadyStr = "";
        System.out.println("Waiting for cluster to become available.");

       try {
        DescribeClustersRequest clustersRequest = DescribeClustersRequest.builder()
                .clusterIdentifier(clusterId)
                .build();

       // Loop until the cluster is ready
         while (!clusterReady) {

             DescribeClustersResponse clusterResponse = redshiftClient.describeClusters(clustersRequest);
             List<Cluster> clusterList = clusterResponse.clusters();

             for (Cluster cluster : clusterList) {

                 clusterReadyStr = cluster.clusterStatus();
                 if (clusterReadyStr.contains("available"))
                     clusterReady = true;
                 else {
                     System.out.print(".");
                     Thread.sleep(sleepTime * 1000);
                 }
             }
         }
           System.out.println("Cluster is available!");

    } catch (RedshiftException | InterruptedException e) {

        System.err.println(e.getMessage());
        System.exit(1);
    }
  }

    public static void modifyCluster(RedshiftClient redshiftClient, String clusterId) {

        try {
        ModifyClusterRequest modifyClusterRequest = ModifyClusterRequest.builder()
                 .clusterIdentifier(clusterId)
                  .preferredMaintenanceWindow("wed:07:30-wed:08:00")
                 .build();

        ModifyClusterResponse clusterResponse = redshiftClient.modifyCluster(modifyClusterRequest);
        System.out.println("The modified cluster was successfully modified and has "+ clusterResponse.cluster().preferredMaintenanceWindow() +" as the maintenance window");

        } catch (RedshiftException e) {

            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[redshift.java2.create_cluster.main]
    }
}
