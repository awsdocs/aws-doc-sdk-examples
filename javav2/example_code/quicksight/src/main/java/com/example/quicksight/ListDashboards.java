//snippet-sourcedescription:[ListDashboards.java demonstrates how to list Amazon QuickSight dashboards.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon QuickSight]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.quicksight;

// snippet-start:[quicksight.java2.list_dashboards.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.DashboardSummary;
import software.amazon.awssdk.services.quicksight.model.ListDashboardsRequest;
import software.amazon.awssdk.services.quicksight.model.ListDashboardsResponse;
import software.amazon.awssdk.services.quicksight.model.QuickSightException;
import java.util.List;
// snippet-end:[quicksight.java2.list_dashboards.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDashboards {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage: " +
            "   <account>\n\n" +
            "Where:\n" +
            "   account - The ID of the AWS account.\n\n";

         if (args.length != 1) {
             System.out.println(usage);
             System.exit(1);
         }

        String account = args[0];
        QuickSightClient qsClient = QuickSightClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listAllDashboards(qsClient, account);
        qsClient.close();
    }

    // snippet-start:[quicksight.java2.list_dashboards.main]
    public static void listAllDashboards(QuickSightClient qsClient, String account) {

        try {
            ListDashboardsRequest dashboardsRequest = ListDashboardsRequest.builder()
                .awsAccountId(account)
                .maxResults(20)
                .build();

            ListDashboardsResponse res = qsClient.listDashboards(dashboardsRequest);
            List<DashboardSummary> dashboards = res.dashboardSummaryList();
            for (DashboardSummary dashboard: dashboards) {
                System.out.println("Dashboard name: "+dashboard.name());
                System.out.println("Dashboard ARN: "+dashboard.arn());
                System.out.println("Dashboard Id: "+dashboard.dashboardId());
            }

        } catch (QuickSightException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[quicksight.java2.list_dashboards.main]
}
