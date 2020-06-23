// snippet-sourcedescription:[ListMigrationTasks.java demonstrates how to list migration tasks.]
// snippet-service:[Amazon Migration Hub]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Migration Hub]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[6-23-2020]
// snippet-sourceauthor:[scmacdon - AWS]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.example.migrationhub;

// snippet-start:[migration.java2.list_migration_tasks.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.ListMigrationTasksRequest;
import software.amazon.awssdk.services.migrationhub.model.ListMigrationTasksResponse;
import software.amazon.awssdk.services.migrationhub.model.MigrationTaskSummary;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
import java.util.Iterator;
import java.util.List;
// snippet-end:[migration.java2.list_migration_tasks.import]

public class ListMigrationTasks {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
                .region(region)
                .build();

        listMigrTasks(migrationClient) ;
   }

    // snippet-start:[migration.java2.list_migration_tasks.main]
    public static void listMigrTasks(MigrationHubClient migrationClient) {

        try{

            ListMigrationTasksRequest listMigrationTasksRequest = ListMigrationTasksRequest.builder()
                    .maxResults(10)
                    .build();

            ListMigrationTasksResponse response = migrationClient.listMigrationTasks(listMigrationTasksRequest);

            // Display the results
            List<MigrationTaskSummary> MigrationList = response.migrationTaskSummaryList();
            Iterator<MigrationTaskSummary> appIterator = MigrationList.iterator();

            while(appIterator.hasNext()) {
                MigrationTaskSummary migration = appIterator.next();
                System.out.println("Migration task name is " +migration.migrationTaskName());
                System.out.println("The Progress update stream is " +migration.progressUpdateStream());
            }

        } catch(MigrationHubException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[migration.java2.list_migration_tasks.main]
    }
}
