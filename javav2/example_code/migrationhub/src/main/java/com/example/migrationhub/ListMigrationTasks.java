// snippet-sourcedescription:[ListMigrationTasks.java demonstrates how to list migration tasks.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS Migration Hub]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.migrationhub;

// snippet-start:[migration.java2.list_migration_tasks.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.ListMigrationTasksRequest;
import software.amazon.awssdk.services.migrationhub.model.ListMigrationTasksResponse;
import software.amazon.awssdk.services.migrationhub.model.MigrationTaskSummary;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
import java.util.List;
// snippet-end:[migration.java2.list_migration_tasks.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListMigrationTasks {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listMigrTasks(migrationClient) ;
        migrationClient.close();
   }

    // snippet-start:[migration.java2.list_migration_tasks.main]
    public static void listMigrTasks(MigrationHubClient migrationClient) {

        try{
            ListMigrationTasksRequest listMigrationTasksRequest = ListMigrationTasksRequest.builder()
                    .maxResults(10)
                    .build();

            ListMigrationTasksResponse response = migrationClient.listMigrationTasks(listMigrationTasksRequest);
            List<MigrationTaskSummary> migrationList = response.migrationTaskSummaryList();
            for (MigrationTaskSummary migration : migrationList) {
                System.out.println("Migration task name is " + migration.migrationTaskName());
                System.out.println("The Progress update stream is " + migration.progressUpdateStream());
            }

        } catch(MigrationHubException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[migration.java2.list_migration_tasks.main]
}
