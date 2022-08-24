//snippet-sourcedescription:[ListThemes.java demonstrates how to list Amazon QuickSight themes.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon QuickSight]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.quicksight;

// snippet-start:[quicksight.java2.list_themes.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.ListThemesRequest;
import software.amazon.awssdk.services.quicksight.model.ListThemesResponse;
import software.amazon.awssdk.services.quicksight.model.ThemeSummary;
import software.amazon.awssdk.services.quicksight.model.QuickSightException;
import java.util.List;
// snippet-end:[quicksight.java2.list_themes.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListThemes {

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

        listAllThemes(qsClient, account);
        qsClient.close();
    }

    // snippet-start:[quicksight.java2.list_themes.main]
    public static void listAllThemes(QuickSightClient qsClient, String account ) {

        try {
            ListThemesRequest themeRequest = ListThemesRequest.builder()
                 .awsAccountId(account)
                 .build();

            ListThemesResponse analysisResponse = qsClient.listThemes(themeRequest);
            List<ThemeSummary> themes = analysisResponse.themeSummaryList();
            for (ThemeSummary theme: themes) {
                System.out.println("Theme id is "+theme.themeId());
                System.out.println("Theme name is "+theme.name());
                System.out.println("Theme ARN is "+theme.arn());
                System.out.println("Theme version is "+theme.latestVersionNumber());
            }

        } catch (QuickSightException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[quicksight.java2.list_themes.main]
}



