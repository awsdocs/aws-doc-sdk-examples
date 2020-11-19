// snippet-sourcedescription:[ImportMigrationTask.java demonstrates how to register a new migration task.]
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

// snippet-start:[migration.java2.import_migration.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.CreateProgressUpdateStreamRequest;
import software.amazon.awssdk.services.migrationhub.model.ImportMigrationTaskRequest;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
// snippet-end:[migration.java2.import_migration.import]

public class ImportMigrationTask {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ImportMigrationTask <migrationTask> <progressStream> \n\n" +
                "Where:\n" +
                "    migrationTask - the name of a migration task. \n"+
                "    progressStream - the name of a progress stream. \n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String migrationTask = args[0];
        String progressStream = args[1];

        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
                .region(region)
                .build();

        importMigrTask(migrationClient, migrationTask, progressStream);
        migrationClient.close();
    }

    // snippet-start:[migration.java2.import_migration.main]
    public static void importMigrTask(MigrationHubClient migrationClient,  String migrationTask, String progressStream) {

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
    // snippet-end:[migration.java2.import_migration.main]
    }
}
