// snippet-sourcedescription:[DescribeMigrationTask.java demonstrates how to get a list of attributes associated with a migration task.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS Migration Hub]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11-05-2020]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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
                "Usage:\n" +
                "    DescribeMigrationTask <migrationTask> <progressStream> \n\n" +
                "Where:\n" +
                "    migrationTask - the name of a migration task. \n"+
                "    progressStream - the name of a progress stream. \n";

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
       migrationClient.close();
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
