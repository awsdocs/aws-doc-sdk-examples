//snippet-sourcedescription:[DeleteDeploymentGroup.java demonstrates how to delete a deployment group.]
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

// snippet-start:[codedeploy.java2.delete_group.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.DeleteDeploymentGroupRequest;
// snippet-end:[codedeploy.java2.delete_group.import]

public class DeleteDeploymentGroup {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteDeploymentGroup <appName><deploymentGroupName>\n\n" +
                "Where:\n" +
                "    appName - the name of the application \n"+
                "    deploymentGroupName - the name of the deployment group \n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String appName = args[0];
        String deploymentGroupName = args[1];

        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
                .region(region)
                .build();

        delDeploymentGroup(deployClient, appName, deploymentGroupName);
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
