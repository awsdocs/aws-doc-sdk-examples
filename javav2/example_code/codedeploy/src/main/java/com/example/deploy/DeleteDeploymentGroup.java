//snippet-sourcedescription:[DeleteDeploymentGroup.java demonstrates how to delete a deployment group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS CodeDeploy]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.deploy;

// snippet-start:[codedeploy.java2.delete_group.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.DeleteDeploymentGroupRequest;
// snippet-end:[codedeploy.java2.delete_group.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteDeploymentGroup {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <appName> <deploymentGroupName>\n\n" +
            "Where:\n" +
            "    appName - The name of the application. \n"+
            "    deploymentGroupName - The name of the deployment group. \n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String appName = args[0];
        String deploymentGroupName = args[1];
        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        delDeploymentGroup(deployClient, appName, deploymentGroupName);
        deployClient.close();
    }

    // snippet-start:[codedeploy.java2.delete_group.main]
    public static void delDeploymentGroup(CodeDeployClient deployClient,
                                          String appName,
                                          String deploymentGroupName) {

        try {
            DeleteDeploymentGroupRequest deleteDeploymentGroupRequest = DeleteDeploymentGroupRequest.builder()
                .deploymentGroupName(appName)
                .applicationName(deploymentGroupName)
                .build();

            deployClient.deleteDeploymentGroup(deleteDeploymentGroupRequest);
            System.out.println(deploymentGroupName +" was deleted!");

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codedeploy.java2.delete_group.main]
}
