//snippet-sourcedescription:[GetDeployment.java demonstrates how to get information about a deployment.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS CodeDeploy
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/3/2020]
//snippet-sourceauthor:[scmacdon AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.deploy;

// snippet-start:[codedeploy.java2._get_deployment.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.GetDeploymentRequest;
import software.amazon.awssdk.services.codedeploy.model.GetDeploymentResponse;
// snippet-end:[codedeploy.java2._get_deployment.import]


public class GetDeployment {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetDeployment <deploymentId> \n\n" +
                "Where:\n" +
                "    deploymentId - the id of the deployment. \n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String deploymentId = args[0];
        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
                .region(region)
                .build();

        getSpecificDeployment(deployClient, deploymentId);
        deployClient.close();
    }

    // snippet-start:[codedeploy.java2._get_deployment.main]
    public static void getSpecificDeployment(CodeDeployClient deployClient, String deploymentId ) {

        try {
            GetDeploymentRequest deploymentRequest = GetDeploymentRequest.builder()
                .deploymentId(deploymentId)
                .build();

            GetDeploymentResponse response = deployClient.getDeployment(deploymentRequest);
            System.out.println("The application associated with this deployment is "+ response.deploymentInfo().applicationName());

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codedeploy.java2._get_deployment.main]
}
