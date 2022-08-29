// snippet-sourcedescription:[DescribeMigrationTask.java demonstrates how to get a list of attributes associated with a migration task.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS Migration Hub]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.migrationhub;

// snippet-start:[migration.java2.describe_migration.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.DescribeMigrationTaskRequest;
import software.amazon.awssdk.services.migrationhub.model.DescribeMigrationTaskResponse;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
// snippet-end:[migration.java2.describe_migration.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeMigrationTask {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    DescribeMigrationTask <migrationTask> <progressStream> \n\n" +
            "Where:\n" +
            "    migrationTask - the name of a migration task. \n"+
            "    progressStream - the name of a progress stream. \n";

        if (args.length < 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String migrationTask = args[0];
        String progressStream = args[1];
        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
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

            DescribeMigrationTaskResponse migrationTaskResponse = migrationClient.describeMigrationTask(migrationTaskRequestRequest);
            System.out.println("The name is "+ migrationTaskResponse.migrationTask().migrationTaskName());

        } catch (MigrationHubException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[migration.java2.describe_migration.main]
}
