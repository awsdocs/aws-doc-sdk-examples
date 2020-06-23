// snippet-sourcedescription:[DescribeMigrationTask.java demonstrates how to get a list of attributes associated with a migration task.]
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

// snippet-start:[migration.java2.describe_migration.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.DescribeMigrationTaskRequest;
import software.amazon.awssdk.services.migrationhub.model.DescribeMigrationTaskResponse;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
// snippet-end:[migration.java2.describe_migration.import]

public class DescribeMigrationTask {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of a migration task and progress stream\n" +
                "\n" +
                "Ex: DescribeMigrationTask SampleApp ProgressSteam\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String migrationTask = args[0];
        String progressStream = args[1];

        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
                .region(region)
                .build();

       describeMigTask(migrationClient, migrationTask, progressStream);
    }

    // snippet-start:[migration.java2.describe_migration.main]
    public static void describeMigTask(MigrationHubClient migrationClient, String migrationTask, String progressStream) {
        try {

            DescribeMigrationTaskRequest migrationTaskRequestRequest = DescribeMigrationTaskRequest.builder()
                    .progressUpdateStream(progressStream)
                    .migrationTaskName(migrationTask)
                    .build();

            DescribeMigrationTaskResponse migrationTaskResponse=  migrationClient.describeMigrationTask(migrationTaskRequestRequest);

            // Display the result
            System.out.println("The name is "+ migrationTaskResponse.migrationTask().migrationTaskName());

        } catch (MigrationHubException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[migration.java2.describe_migration.main]
    }
}
