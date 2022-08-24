// snippet-sourcedescription:[ListCreatedArtifacts.java demonstrates how to List the created artifacts attached to a given migration task.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS Migration Hub]
// snippet-keyword:[Code Sample]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.migrationhub;

// snippet-start:[migration.java2.list_artifacts.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.CreatedArtifact;
import software.amazon.awssdk.services.migrationhub.model.ListCreatedArtifactsRequest;
import software.amazon.awssdk.services.migrationhub.model.ListCreatedArtifactsResponse;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
import java.util.List;
// snippet-end:[migration.java2.list_artifacts.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListCreatedArtifacts {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listArtifacts(migrationClient);
        migrationClient.close();
    }

    // snippet-start:[migration.java2.list_artifacts.main]
    public static void listArtifacts(MigrationHubClient migrationClient) {

        try {
            ListCreatedArtifactsRequest listCreatedArtifactsRequest = ListCreatedArtifactsRequest.builder()
                .maxResults(10)
                .migrationTaskName("SampleApp5")
                .progressUpdateStream("ProgressSteamB")
                .build();

            ListCreatedArtifactsResponse response = migrationClient.listCreatedArtifacts(listCreatedArtifactsRequest);
            List<CreatedArtifact> apps = response.createdArtifactList();
            for (CreatedArtifact artifact : apps) {
                System.out.println("APp Id is " + artifact.description());
                System.out.println("The name is " + artifact.name());
            }

        } catch(MigrationHubException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[migration.java2.list_artifacts.main]
 }
