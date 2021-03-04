//snippet-sourcedescription:[DescribeDBInstances.java demonstrates how to obtain information about a dashboard.]
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

// snippet-start:[quicksight.java2.describe_dashboard.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.DescribeDashboardRequest;
import software.amazon.awssdk.services.quicksight.model.DescribeDashboardResponse;
import software.amazon.awssdk.services.quicksight.model.QuickSightException;
// snippet-end:[quicksight.java2.describe_dashboard.import]

public class DescribeDashboard {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: DescribeDashboard <account> <dashboardId>\n\n" +
                "Where:\n" +
                "  account - the ID of the AWS account.\n\n"+
                "  dashboardId - the ID of the Amazon QuickSight Dashboard to describe.\n\n";

         if (args.length != 2) {
             System.out.println(USAGE);
             System.exit(1);
         }

        String account = args[0];
        String dashboardId = args[1];
        QuickSightClient qsClient = QuickSightClient.builder()
                .region(Region.US_EAST_1)
                .build();

        describeSpecificDashboard(qsClient, account, dashboardId);
        qsClient.close();
    }

    // snippet-start:[quicksight.java2.describe_dashboard.main]
    public static void describeSpecificDashboard(QuickSightClient qsClient, String account, String dashboardId) {

        try {

            DescribeDashboardRequest analysesRequest = DescribeDashboardRequest.builder()
                    .awsAccountId(account)
                    .dashboardId(dashboardId)
                    .build();

            DescribeDashboardResponse res  = qsClient.describeDashboard(analysesRequest);
            System.out.println("The display name is " + res.dashboard().name());

        } catch (QuickSightException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[quicksight.java2.describe_dashboard.main]
}

