// snippet-sourcedescription:[ListApplications.java demonstrates how to list applications.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS Migration Hub]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.migrationhub;

// snippet-start:[migration.java2.list_apps.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.ApplicationState;
import software.amazon.awssdk.services.migrationhub.model.ListApplicationStatesRequest;
import software.amazon.awssdk.services.migrationhub.model.ListApplicationStatesResponse ;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
import java.util.List;
// snippet-end:[migration.java2.list_apps.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListApplications {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listApps(migrationClient);
        migrationClient.close();
    }

    // snippet-start:[migration.java2.list_apps.main]
    public static void listApps(MigrationHubClient migrationClient) {

        try{
            ListApplicationStatesRequest applicationStatesRequest = ListApplicationStatesRequest.builder()
                .maxResults(10)
                .build();

            ListApplicationStatesResponse response = migrationClient.listApplicationStates(applicationStatesRequest);
            List<ApplicationState> apps = response.applicationStateList();
            for (ApplicationState appState : apps) {
                System.out.println("App Id is " + appState.applicationId());
                System.out.println("The status is " + appState.applicationStatus().toString());
            }

        } catch(MigrationHubException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[migration.java2.list_apps.main]
}
