//snippet-sourcedescription:[UpdateDashboard.java demonstrates how to update an Amazon QuickSight dashboard.]
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

// snippet-start:[quicksight.java2.update_dashboard.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.DataSetReference;
import software.amazon.awssdk.services.quicksight.model.DashboardSourceTemplate;
import software.amazon.awssdk.services.quicksight.model.DashboardSourceEntity;
import software.amazon.awssdk.services.quicksight.model.UpdateDashboardRequest;
import software.amazon.awssdk.services.quicksight.model.UpdateDashboardResponse;
import software.amazon.awssdk.services.quicksight.model.QuickSightException;
import software.amazon.awssdk.services.quicksight.model.TemplateSourceAnalysis;
import software.amazon.awssdk.services.quicksight.model.TemplateSourceEntity;
import software.amazon.awssdk.services.quicksight.model.CreateTemplateRequest;
import software.amazon.awssdk.services.quicksight.model.CreateTemplateResponse;
import java.util.UUID;
// snippet-end:[quicksight.java2.update_dashboard.import]

/*
*    Before running this code example, follow the Getting Started with Data Analysis in Amazon QuickSight located here:
*
*    https://docs.aws.amazon.com/quicksight/latest/user/getting-started.html
*
*    This code example uses resources that you created by following that topic such as the DataSet Arn value.
*
*
*  Also, set up your development environment, including your credentials.
*
*  For information, see this documentation topic:
*
*  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
*/

public class UpdateDashboard {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <account> <dashboardId> <dataSetArn> <analysisArn>\n\n" +
                "Where:\n" +
                "   account - The account to use.\n\n" +
                "   dashboardId - The dashboard id value to use.\n\n" +
                "   dataSetArn - The ARN of the dataset.\n\n" +
                "   analysisArn - The ARN of an existing analysis";

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String account = args[0];
        String dashboardId = args[1];
        String dataSetArn = args[2];
        String analysisArn = args[3];
        QuickSightClient qsClient = QuickSightClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        updateSpecificDashboard(qsClient, account, dashboardId, dataSetArn, analysisArn);
        qsClient.close();
    }

    // snippet-start:[quicksight.java2.update_dashboard.main]
    public static void updateSpecificDashboard( QuickSightClient qsClient, String account, String dashboardId, String dataSetArn, String analysisArn) {

        try {
            DataSetReference dataSetReference = DataSetReference.builder()
                    .dataSetArn(dataSetArn)
                    .dataSetPlaceholder("Dataset placeholder2")
                    .build();

            // Get a template ARN to use.
            String arn = getTemplateARN(qsClient, account, dataSetArn, analysisArn);
            DashboardSourceTemplate sourceTemplate = DashboardSourceTemplate.builder()
                    .dataSetReferences(dataSetReference)
                   .arn(arn)
                    .build();

            DashboardSourceEntity sourceEntity = DashboardSourceEntity.builder()
                    .sourceTemplate(sourceTemplate)
                    .build();

            UpdateDashboardRequest dashboardRequest = UpdateDashboardRequest.builder()
                    .awsAccountId(account)
                    .dashboardId(dashboardId)
                    .name("UpdateTest")
                     .sourceEntity(sourceEntity)
                    .themeArn("arn:aws:quicksight::aws:theme/MIDNIGHT")
                    .build();

            UpdateDashboardResponse response = qsClient.updateDashboard(dashboardRequest);
            System.out.println("Dashboard " + response.dashboardId() + " has been updated");

        } catch (QuickSightException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    private static String getTemplateARN(QuickSightClient qsClient, String account, String dataset, String analysisArn) {

        String arn = null;
        try {
            DataSetReference setReference = DataSetReference.builder()
                    .dataSetArn(dataset)
                    .dataSetPlaceholder("Dataset placeholder2")
                    .build();

            TemplateSourceAnalysis templateSourceAnalysis = TemplateSourceAnalysis.builder()
                    .dataSetReferences(setReference)
                    .arn(analysisArn)
                    .build();

            TemplateSourceEntity sourceEntity = TemplateSourceEntity.builder()
                    .sourceAnalysis(templateSourceAnalysis)
                    .build();

            UUID uuid = UUID.randomUUID();
            String templateGUID = uuid.toString();
            CreateTemplateRequest createTemplateRequest = CreateTemplateRequest.builder()
               .awsAccountId(account)
               .name("NewTemplate")
               .sourceEntity(sourceEntity)
               .templateId(templateGUID) // Specify a GUID value
               .build();

            CreateTemplateResponse response = qsClient.createTemplate(createTemplateRequest);
            arn = response.arn();

        } catch (QuickSightException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return arn;
    }
    // snippet-end:[quicksight.java2.update_dashboard.main]
}