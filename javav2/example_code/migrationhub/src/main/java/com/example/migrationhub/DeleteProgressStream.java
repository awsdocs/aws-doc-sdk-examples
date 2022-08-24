// snippet-sourcedescription:[DeleteProgressStream.java demonstrates how to delete a progress stream.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS Migration Hub]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.migrationhub;

// snippet-start:[migration.java2.delete_progress_stream.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.DeleteProgressUpdateStreamRequest;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
// snippet-end:[migration.java2.delete_progress_stream.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteProgressStream {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <progressStream> \n\n" +
            "Where:\n" +
            "    progressStream - the name of a progress stream to delete. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String progressStream = args[0];
        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

       deleteStream(migrationClient,progressStream ) ;
       migrationClient.close();
    }

    // snippet-start:[migration.java2.delete_progress_stream.main]
    public static void deleteStream(MigrationHubClient migrationClient,String streamName ) {

        try {
            DeleteProgressUpdateStreamRequest deleteProgressUpdateStreamRequest = DeleteProgressUpdateStreamRequest.builder()
                .progressUpdateStreamName(streamName)
                .build();

            migrationClient.deleteProgressUpdateStream(deleteProgressUpdateStreamRequest);
            System.out.println(streamName + " is deleted" );

        } catch(MigrationHubException e) {
           System.out.println(e.getMessage());
           System.exit(1);
        }
    }
    // snippet-end:[migration.java2.delete_progress_stream.main]
}
