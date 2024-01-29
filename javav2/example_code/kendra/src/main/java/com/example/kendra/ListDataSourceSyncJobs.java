// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kendra;

// snippet-start:[kendra.java2.list.sync.main]
// snippet-start:[kendra.java2.list.sync.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.DataSourceSyncJob;
import software.amazon.awssdk.services.kendra.model.KendraException;
import software.amazon.awssdk.services.kendra.model.ListDataSourceSyncJobsRequest;
import software.amazon.awssdk.services.kendra.model.ListDataSourceSyncJobsResponse;
import java.util.List;
// snippet-end:[kendra.java2.list.sync.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDataSourceSyncJobs {

    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <indexId> <dataSourceId>\s

                Where:
                    indexId - The id value of the index.
                    dataSourceId - The id value of the data source.
                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String indexId = args[0];
        String dataSourceId = args[1];
        System.out.printf("Gets statistics about synchronizing Amazon Kendra with a data source %s%n", dataSourceId);
        KendraClient kendra = KendraClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listSyncJobs(kendra, indexId, dataSourceId);
    }

    public static void listSyncJobs(KendraClient kendra, String indexId, String dataSourceId) {
        try {
            ListDataSourceSyncJobsRequest jobsRequest = ListDataSourceSyncJobsRequest.builder()
                    .indexId(indexId)
                    .maxResults(10)
                    .id(dataSourceId)
                    .build();

            ListDataSourceSyncJobsResponse response = kendra.listDataSourceSyncJobs(jobsRequest);
            List<DataSourceSyncJob> jobs = response.history();
            for (DataSourceSyncJob job : jobs) {
                System.out.println("Execution id is " + job.executionId());
                System.out.println("Job status " + job.status());
            }

        } catch (KendraException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[kendra.java2.list.sync.main]
