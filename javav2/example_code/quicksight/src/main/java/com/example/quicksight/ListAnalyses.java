// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.quicksight;

// snippet-start:[quicksight.java2.list_analyses.main]
// snippet-start:[quicksight.java2.list_analyses.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.ListAnalysesRequest;
import software.amazon.awssdk.services.quicksight.model.ListAnalysesResponse;
import software.amazon.awssdk.services.quicksight.model.AnalysisSummary;
import software.amazon.awssdk.services.quicksight.model.QuickSightException;
import java.util.List;
// snippet-end:[quicksight.java2.list_analyses.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListAnalyses {
    public static void main(String[] args) {
        final String usage = """

                Usage:    <account>

                Where:
                   account - The ID of the AWS account.

                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String account = args[0];
        QuickSightClient qsClient = QuickSightClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listAllAnAnalyses(qsClient, account);
        qsClient.close();
    }

    public static void listAllAnAnalyses(QuickSightClient qsClient, String account) {
        try {
            ListAnalysesRequest analysesRequest = ListAnalysesRequest.builder()
                    .awsAccountId(account)
                    .maxResults(20)
                    .build();

            ListAnalysesResponse res = qsClient.listAnalyses(analysesRequest);
            List<AnalysisSummary> analysisList = res.analysisSummaryList();
            for (AnalysisSummary analysis : analysisList) {
                System.out.println("Analysis name: " + analysis.name());
                System.out.println("Analysis status: " + analysis.status());
                System.out.println("Analysis status: " + analysis.analysisId());
            }

        } catch (QuickSightException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[quicksight.java2.list_analyses.main]
