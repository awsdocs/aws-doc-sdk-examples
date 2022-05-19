//snippet-sourcedescription:[GetDeployment.java demonstrates how to get information about a deployment.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS CodeDeploy
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/17/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.deploy;

// snippet-start:[codedeploy.java2._get_deployment.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.GetDeploymentRequest;
import software.amazon.awssdk.services.codedeploy.model.GetDeploymentResponse;
// snippet-end:[codedeploy.java2._get_deployment.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetDeployment {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <deploymentId> \n\n" +
                "Where:\n" +
                "    deploymentId - The id of the deployment. \n";

       if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String deploymentId = args[0];
        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
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
