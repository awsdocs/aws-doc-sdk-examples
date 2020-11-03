//snippet-sourcedescription:[DeleteApplication.java demonstrates how to delete an application.]
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

// snippet-start:[codedeploy.java2.delete_app.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.DeleteApplicationRequest;
// snippet-end:[codedeploy.java2.delete_app.import]

public class DeleteApplication {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteApplication <appName> \n\n" +
                "Where:\n" +
                "    appName -  the name of the application \n";

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

        // Delete the specified application
        delApplication(deployClient, appName);
    }

    // snippet-start:[codedeploy.java2.delete_app.main]
    public static void delApplication(CodeDeployClient deployClient, String appName) {

        try {

            DeleteApplicationRequest deleteApplicationRequest = DeleteApplicationRequest.builder()
                .applicationName(appName)
                .build();

            deployClient.deleteApplication(deleteApplicationRequest);
            System.out.println(appName +" was deleted!");

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codedeploy.java2.delete_app.main]
}
