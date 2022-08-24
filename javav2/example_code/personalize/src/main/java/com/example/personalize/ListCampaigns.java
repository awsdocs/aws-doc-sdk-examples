//snippet-sourcedescription:[ListCampaigns.java demonstrates how to list Amazon Personalize campaigns.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Personalize]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.personalize;

//snippet-start:[personalize.java2.list_campaigns.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CampaignSummary;
import software.amazon.awssdk.services.personalize.model.ListCampaignsRequest;
import software.amazon.awssdk.services.personalize.model.ListCampaignsResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import java.util.List;
//snippet-end:[personalize.java2.list_campaigns.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListCampaigns {

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "Usage:\n" +
            "    ListCampaigns <solutionArn>\n\n" +
            "Where:\n" +
            "    solutionArn - The ARN of the solution.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String solutionArn = args[0];
        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
            .region(region)
            .build();

        listAllCampaigns(personalizeClient, solutionArn);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.list_campaigns.main]
    public static void listAllCampaigns(PersonalizeClient personalizeClient, String solutionArn) {

        try{
            ListCampaignsRequest campaignsRequest = ListCampaignsRequest.builder()
                .maxResults(10)
                .solutionArn(solutionArn)
                .build();

            ListCampaignsResponse response = personalizeClient.listCampaigns(campaignsRequest);
            List<CampaignSummary> campaigns = response.campaigns();
            for (CampaignSummary campaign: campaigns) {
                System.out.println("Campaign name is : "+campaign.name());
                System.out.println("Campaign ARN is : "+campaign.campaignArn());
            }

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.list_campaigns.main]
}
