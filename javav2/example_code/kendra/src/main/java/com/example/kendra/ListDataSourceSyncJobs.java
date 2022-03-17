//snippet-sourcedescription:[ListDataSourceSyncJobs.java demonstrates how to get statistics about synchronizing Amazon Kendra with a data source.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kendra]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/10/2022]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kendra;

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
 * To run this Java V2 code example, ensure that you have set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */
public class ListDataSourceSyncJobs {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <indexId> <dataSourceId> \n\n" +
                "Where:\n" +
                "    indexId - The id value of the index.\n" +
                "    dataSourceId - The id value of the data source.\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String indexId = args[0];
        String dataSourceId = args[1];
        System.out.println(String.format("Gets statistics about synchronizing Amazon Kendra with a data source %s", dataSourceId));
        KendraClient kendra = KendraClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listSyncJobs(kendra, indexId, dataSourceId);
    }

   // snippet-start:[kendra.java2.list.sync.main]
   public static void listSyncJobs( KendraClient kendra, String indexId, String dataSourceId){

        try {
            ListDataSourceSyncJobsRequest jobsRequest = ListDataSourceSyncJobsRequest.builder()
                    .indexId(indexId)
                    .maxResults(10)
                    .id(dataSourceId)
                    .build();

            ListDataSourceSyncJobsResponse response = kendra.listDataSourceSyncJobs(jobsRequest);
            List<DataSourceSyncJob> jobs = response.history();
            for (DataSourceSyncJob job: jobs) {
                System.out.println("Execution id is "+job.executionId());
                System.out.println("Job status "+job.status());
            }

        } catch (KendraException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
   }
// snippet-end:[kendra.java2.list.sync.main]
}
