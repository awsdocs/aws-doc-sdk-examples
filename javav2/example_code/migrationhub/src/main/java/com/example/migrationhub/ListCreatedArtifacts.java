// snippet-sourcedescription:[ListCreatedArtifacts.java demonstrates how to List the created artifacts attached to a given migration task.]
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

// snippet-start:[migration.java2.list_artifacts.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.CreatedArtifact;
import software.amazon.awssdk.services.migrationhub.model.ListCreatedArtifactsRequest;
import software.amazon.awssdk.services.migrationhub.model.ListCreatedArtifactsResponse;
import software.amazon.awssdk.services.migrationhub.model.MigrationHubException;
import java.util.Iterator;
import java.util.List;
// snippet-end:[migration.java2.list_artifacts.import]

public class ListCreatedArtifacts {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        MigrationHubClient migrationClient = MigrationHubClient.builder()
                .region(region)
                .build();

        listArtifacts(migrationClient);
    }

    // snippet-start:[migration.java2.list_artifacts.main]
    public static void listArtifacts(MigrationHubClient migrationClient) {

        try {

            ListCreatedArtifactsRequest listCreatedArtifactsRequest = ListCreatedArtifactsRequest.builder()
                    .maxResults(10)
                    .migrationTaskName("SampleApp5")
                    .progressUpdateStream("ProgressStreamB")
                    .build();

            ListCreatedArtifactsResponse response = migrationClient.listCreatedArtifacts(listCreatedArtifactsRequest);
            List<CreatedArtifact> apps = response.createdArtifactList();

            Iterator<CreatedArtifact> appIterator = apps.iterator();

            while(appIterator.hasNext()) {
                CreatedArtifact artifact = appIterator.next();
                System.out.println("App ID is " +artifact.description());
                System.out.println("The name is " +artifact.name());
            }

    } catch(MigrationHubException e) {
        System.out.println(e.getMessage());
        System.exit(1);
    }
   // snippet-end:[migration.java2.list_artifacts.main]
  }
 }
