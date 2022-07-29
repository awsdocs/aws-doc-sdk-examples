//snippet-sourcedescription:[UpdateService.java demonstrates how to update the task placement strategies and constraints on an Amazon Elastic Container Service (Amazon ECS) service.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Elastic Container Service]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ecs;

// snippet-start:[ecs.java2.update_service.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.EcsException;
import software.amazon.awssdk.services.ecs.model.UpdateServiceRequest;
// snippet-end:[ecs.java2.update_service.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class UpdateService {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "   <clusterName> <serviceArn> \n\n" +
            "Where:\n" +
            "  clusterName - The cluster name.\n" +
            "  serviceArn - The service ARN value.\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String clusterName = args[0];
        String serviceArn = args[1];
        Region region = Region.US_EAST_1;
        EcsClient ecsClient = EcsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        updateSpecificService(ecsClient, clusterName, serviceArn );
        ecsClient.close();
    }

    // snippet-start:[ecs.java2.update_service.main]
    public static void updateSpecificService( EcsClient ecsClient, String clusterName, String serviceArn) {

        try {
            UpdateServiceRequest serviceRequest = UpdateServiceRequest.builder()
                .cluster(clusterName)
                .service(serviceArn)
                .desiredCount(0)
                .build();

            ecsClient.updateService(serviceRequest);
            System.out.println("The service was modified");

        } catch (EcsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ecs.java2.update_service.main]
}