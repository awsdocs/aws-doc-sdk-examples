//snippet-sourcedescription:[ListDeploymentGroups.java demonstrates how to list your deployment groups.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS CodeDeploy
//snippet-service:[AWS CodeDeploy]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/3/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.deploy;

// snippet-start:[codedeploy.java2._list_groups.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.ListDeploymentGroupsRequest;
import software.amazon.awssdk.services.codedeploy.model.ListDeploymentGroupsResponse;
// snippet-end:[codedeploy.java2._list_groups.import]

import java.util.List;

public class ListDeploymentGroups {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListDeploymentGroups <appName> \n\n" +
                "Where:\n" +
                "    appName - the application name \n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String appName = args[0];

        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
                .region(region)
                .build();

        listDeployGroups(deployClient, appName);
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