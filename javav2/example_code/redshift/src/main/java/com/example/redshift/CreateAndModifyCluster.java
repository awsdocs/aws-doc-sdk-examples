//snippet-sourcedescription:[CreateAndModifyCluster.java demonstrates how to create and modify an Amazon Redshift cluster.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Redshift]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.redshift;

// snippet-start:[redshift.java2.create_cluster.import]
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
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
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.List;
// snippet-end:[redshift.java2.create_cluster.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This example requires an AWS Secrets Manager secret that contains the database credentials. If you do not create a
 * secret, this example will not work. For details, see:
 *
 * https://docs.aws.amazon.com/secretsmanager/latest/userguide/integrating_how-services-use-secrets_RS.html
 */

public class CreateAndModifyCluster {

    public static long sleepTime = 20;

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <clusterId> <masterUsername> <masterUserPassword> \n\n" +
            "Where:\n" +
            "    clusterId - The id of the cluster to create. \n" +
            "    secretName - The name of the AWS Secrets Manager secret that contains the database credentials" ;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String clusterId = args[0];
        String secretName = args[0];
        Gson gson = new Gson();
        User user = gson.fromJson(String.valueOf(getSecretValues(secretName)), User.class);
        Region region = Region.US_WEST_2;
        RedshiftClient redshiftClient = RedshiftClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createCluster(redshiftClient,clusterId, user.getMasterUsername(), user.getMasterUserPassword() );
        waitForClusterReady(redshiftClient, clusterId);
        modifyCluster(redshiftClient, clusterId);
        redshiftClient.close();
    }

    public static String getSecretValues(String secretName) {
        SecretsManagerClient secretClient = getSecretClient();
        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }
    private static SecretsManagerClient getSecretClient() {
        Region region = Region.US_WEST_2;
        return SecretsManagerClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
    }

    // snippet-start:[redshift.java2.create_cluster.main]
    public static void createCluster(RedshiftClient redshiftClient, String clusterId, String masterUsername, String masterUserPassword ) {
        try {
            CreateClusterRequest clusterRequest = CreateClusterRequest.builder()
                .clusterIdentifier(clusterId)
                .masterUsername(masterUsername) // set the user name here
                .masterUserPassword(masterUserPassword) // set the user password here
                .nodeType("dc2.large")
                .publiclyAccessible(true)
                .numberOfNodes(2)
                .build();

        CreateClusterResponse clusterResponse = redshiftClient.createCluster(clusterRequest);
        System.out.println("Created cluster " + clusterResponse.cluster().clusterIdentifier());

       } catch (RedshiftException e) {

           System.err.println(e.getMessage());
           System.exit(1);
       }
    }
    // snippet-end:[redshift.java2.create_cluster.main]


    // Waits until the cluster is available
    public static void waitForClusterReady(RedshiftClient redshiftClient, String clusterId) {

        Boolean clusterReady = false;
        String clusterReadyStr;
        System.out.println("Waiting for cluster to become available.");

        try {
            DescribeClustersRequest clustersRequest = DescribeClustersRequest.builder()
                .clusterIdentifier(clusterId)
                .build();

            // Loop until the cluster is ready.
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

    // snippet-start:[redshift.java2.mod_cluster.main]
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
    }
    // snippet-end:[redshift.java2.mod_cluster.main]

}
