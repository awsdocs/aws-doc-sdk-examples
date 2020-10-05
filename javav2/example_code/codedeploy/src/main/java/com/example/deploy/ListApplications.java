//snippet-sourcedescription:[ListApplications.java demonstrates how to information about your applications.]
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

// snippet-start:[codedeploy.java2._list_apps.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.ListApplicationsResponse;
// snippet-end:[codedeploy.java2._list_apps.import]

import java.util.List;

public class ListApplications {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
                .region(region)
                .build();

        listApps(deployClient);
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
