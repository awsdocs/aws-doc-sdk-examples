//snippet-sourcedescription:[CreateCluster.java demonstrates how to create a cluster for the Amazon Elastic Container Service (Amazon ECS) service.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Elastic Container Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ecs;

// snippet-start:[ecs.java2.create_cluster.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.ExecuteCommandConfiguration;
import software.amazon.awssdk.services.ecs.model.ExecuteCommandLogging;
import software.amazon.awssdk.services.ecs.model.ClusterConfiguration;
import software.amazon.awssdk.services.ecs.model.CreateClusterResponse;
import software.amazon.awssdk.services.ecs.model.EcsException;
import software.amazon.awssdk.services.ecs.model.CreateClusterRequest;
// snippet-end:[ecs.java2.create_cluster.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateCluster {
    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "   <clusterName> \n\n" +
            "Where:\n" +
            "   clusterName - The name of the ECS cluster to create.\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String clusterName = args[0];
        Region region = Region.US_EAST_1;
        EcsClient ecsClient = EcsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        String clusterArn = createGivenCluster(ecsClient, clusterName);
        System.out.println("The cluster ARN is "+clusterArn) ;
        ecsClient.close();
    }

    // snippet-start:[ecs.java2.create_cluster.main]
    public static String createGivenCluster( EcsClient ecsClient, String clusterName) {

        try {
            ExecuteCommandConfiguration commandConfiguration = ExecuteCommandConfiguration.builder()
                .logging(ExecuteCommandLogging.DEFAULT)
                .build();

            ClusterConfiguration clusterConfiguration = ClusterConfiguration.builder()
                .executeCommandConfiguration(commandConfiguration)
                .build();

            CreateClusterRequest clusterRequest = CreateClusterRequest.builder()
                .clusterName(clusterName)
                .configuration(clusterConfiguration)
                .build();

            CreateClusterResponse response = ecsClient.createCluster(clusterRequest) ;
            return response.cluster().clusterArn();

        } catch (EcsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[ecs.java2.create_cluster.main]
}
