//snippet-sourcedescription:[ListApplications.java demonstrates how to information about your applications.]
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

// snippet-start:[codedeploy.java2._list_apps.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.ListApplicationsResponse;
import java.util.List;
// snippet-end:[codedeploy.java2._list_apps.import]

public class ListApplications {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
                .region(region)
                .build();

        listApps(deployClient);
        deployClient.close();
    }

    // snippet-start:[codedeploy.java2._list_apps.main]
    public static void listApps(CodeDeployClient deployClient) {

        try {
            ListApplicationsResponse applicationsResponse = deployClient.listApplications();
            List<String> apps = applicationsResponse.applications();

            for (String app: apps) {
                System.out.println("The application name is: "+app);
            }

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codedeploy.java2._list_apps.main]
}
