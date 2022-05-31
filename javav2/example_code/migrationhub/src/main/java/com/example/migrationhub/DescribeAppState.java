// snippet-sourcedescription:[DescribeAppState.java demonstrates how to get the migration status of an application.]
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

// snippet-start:[migration.java2.describe_app_state.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.DescribeApplicationStateRequest;
import software.amazon.awssdk.services.migrationhub.model.DescribeApplicationStateResponse;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
// snippet-end:[migration.java2.describe_app_state.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeAppState {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    DescribeAppState <appId> \n\n" +
                "Where:\n" +
                "    appId -  the application id value. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String appId = args[0];
        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        describeApplicationState(migrationClient, appId);
        migrationClient.close();
    }

    // snippet-start:[migration.java2.describe_app_state.main]
    public static void describeApplicationState (MigrationHubClient migrationClient, String appId) {

        try {
            DescribeApplicationStateRequest applicationStateRequest = DescribeApplicationStateRequest.builder()
                    .applicationId(appId)
                    .build();

            DescribeApplicationStateResponse applicationStateResponse = migrationClient.describeApplicationState(applicationStateRequest);
            System.out.println("The application status is " +applicationStateResponse.applicationStatusAsString() );

        } catch(MigrationHubException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[migration.java2.describe_app_state.main]
}
