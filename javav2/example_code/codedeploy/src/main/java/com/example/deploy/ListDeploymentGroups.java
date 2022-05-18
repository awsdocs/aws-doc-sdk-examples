//snippet-sourcedescription:[ListDeploymentGroups.java demonstrates how to list your deployment groups.]
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

// snippet-start:[codedeploy.java2._list_groups.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.ListDeploymentGroupsRequest;
import software.amazon.awssdk.services.codedeploy.model.ListDeploymentGroupsResponse;
import java.util.List;
// snippet-end:[codedeploy.java2._list_groups.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDeploymentGroups {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <appName> \n\n" +
                "Where:\n" +
                "    appName - The application name. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String appName = args[0];
        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listDeployGroups(deployClient, appName);
        deployClient.close();
    }

    // snippet-start:[codedeploy.java2._list_groups.main]
    public static void listDeployGroups(CodeDeployClient deployClient, String appName) {

        try {
            ListDeploymentGroupsRequest groupsRequest = ListDeploymentGroupsRequest.builder()
                .applicationName(appName)
                .build();

            ListDeploymentGroupsResponse response = deployClient.listDeploymentGroups(groupsRequest);
            List<String> groups = response.deploymentGroups();
            for (String group: groups) {
                System.out.println("The deployment group is: "+group);
            }

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
   }
    // snippet-end:[codedeploy.java2._list_groups.main]
}