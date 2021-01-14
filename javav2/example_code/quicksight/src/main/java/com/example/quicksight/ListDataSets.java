//snippet-sourcedescription:[ListDataSets.java demonstrates how to list Amazon QuickSight datasets.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon QuickSight]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[1/14/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.quicksight;

// snippet-start:[quicksight.java2.list_datasets.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.ListDataSetsRequest;
import software.amazon.awssdk.services.quicksight.model.ListDataSetsResponse;
import software.amazon.awssdk.services.quicksight.model.DataSetSummary;
import software.amazon.awssdk.services.quicksight.model.QuickSightException;
import java.util.List;
// snippet-start:[quicksight.java2.list_datasets.import]

public class ListDataSets {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: ListDataSets <account>\n\n" +
                "Where:\n" +
                "  account - the ID of the AWS account.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String account = args[0];
        QuickSightClient qsClient = QuickSightClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listAllDataSets(qsClient, account);
        qsClient.close();

    }

    // snippet-start:[quicksight.java2.list_datasets.main]
    public static void listAllDataSets(QuickSightClient qsClient, String account) {

        try {

            ListDataSetsRequest datasetRequest = ListDataSetsRequest.builder()
                    .awsAccountId(account)
                    .maxResults(20)
                    .build();

            ListDataSetsResponse res  = qsClient.listDataSets(datasetRequest);
            List<DataSetSummary> dataSets = res.dataSetSummaries();

            for (DataSetSummary dataset: dataSets) {
                System.out.println("Dataset name: "+dataset.name());
                System.out.println("Dataset ARN: "+dataset.arn());
            }

        } catch (QuickSightException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[quicksight.java2.list_datasets.main]
}
