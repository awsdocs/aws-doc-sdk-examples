//snippet-sourcedescription:[DescribeAnalysis.java demonstrates how to obtain a summary of the metadata for an analysis.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon QuickSight]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.quicksight;

// snippet-start:[quicksight.java2.describe_analysis.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.DescribeAnalysisRequest;
import software.amazon.awssdk.services.quicksight.model.DescribeAnalysisResponse;
import software.amazon.awssdk.services.quicksight.model.QuickSightException;
// snippet-end:[quicksight.java2.describe_analysis.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeAnalysis {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <account> <analysisId>\n\n" +
                "Where:\n" +
                "   account - The ID of the AWS account that contains the analysis.\n\n" +
                "   queueName - The ID of the Amazon QuickSight Analysis that you're describing.\n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String account = args[0];
        String analysisId = args[1];
        QuickSightClient qsClient = QuickSightClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        describeSpecificAnalysis(qsClient, account, analysisId);
        qsClient.close();
    }

    // snippet-start:[quicksight.java2.describe_analysis.main]
    public static void describeSpecificAnalysis(QuickSightClient qsClient, String account,String analysisId) {

        try {
            DescribeAnalysisRequest analysisRequest = DescribeAnalysisRequest.builder()
                    .awsAccountId(account)
                    .analysisId(analysisId)
                    .build();

            DescribeAnalysisResponse analysisResponse = qsClient.describeAnalysis(analysisRequest);
            System.out.println("The Analysis ARN value is "+ analysisResponse.analysis().arn());
            System.out.println("The Analysis ARN name is "+ analysisResponse.analysis().name());
            System.out.println("The Analysis theme ARN value is "+ analysisResponse.analysis().themeArn());
            System.out.println("The Analysis dataSet ARN  is "+ analysisResponse.analysis().dataSetArns());

        } catch (QuickSightException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[quicksight.java2.describe_analysis.main]
}

