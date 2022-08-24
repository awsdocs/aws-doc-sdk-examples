// snippet-sourcedescription:[ImportMigrationTask.java demonstrates how to register a new migration task.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS Migration Hub]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.migrationhub;

// snippet-start:[migration.java2.import_migration.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.CreateProgressUpdateStreamRequest;
import software.amazon.awssdk.services.migrationhub.model.ImportMigrationTaskRequest;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
// snippet-end:[migration.java2.import_migration.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ImportMigrationTask {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <migrationTask> <progressStream> \n\n" +
            "Where:\n" +
            "    migrationTask - the name of a migration task. \n"+
            "    progressStream - the name of a progress stream. \n";

        if (args.length != 2) {
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

        importMigrTask(migrationClient, migrationTask, progressStream);
        migrationClient.close();
    }

    // snippet-start:[migration.java2.import_migration.main]
    public static void importMigrTask(MigrationHubClient migrationClient, String migrationTask, String progressStream) {

        try {
            CreateProgressUpdateStreamRequest progressUpdateStreamRequest = CreateProgressUpdateStreamRequest.builder()
                .progressUpdateStreamName(progressStream)
                .dryRun(false)
                .build();

           migrationClient.createProgressUpdateStream(progressUpdateStreamRequest);
           ImportMigrationTaskRequest migrationTaskRequest = ImportMigrationTaskRequest.builder()
               .migrationTaskName(migrationTask)
               .progressUpdateStream(progressStream)
               .dryRun(false)
               .build();

           migrationClient.importMigrationTask(migrationTaskRequest);

        } catch(MigrationHubException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[migration.java2.import_migration.main]
}
