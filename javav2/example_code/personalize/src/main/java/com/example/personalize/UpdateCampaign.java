//snippet-sourcedescription:[UpdateCampaign.java demonstrates how to update an Amazon Personalize campaign.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/13/2021]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.update_campaign.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.Campaign;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.UpdateCampaignRequest;
import software.amazon.awssdk.services.personalize.model.DescribeCampaignRequest;
import software.amazon.awssdk.services.personalize.model.DescribeCampaignResponse;
//snippet-end:[personalize.java2.update_campaign.import]

public class UpdateCampaign {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    UpdateCampaign <campaignArn, solutionVersion, minProvisionedTPS>\n\n" +
                "Where:\n" +
                "    campaignArn - The Amazon Resource Name (ARN) of the campaign to update.\n" +
                "    solutionVersion - The Amazon Resource Name (ARN) of the new solution version to deploy.\n" +
                "    minProvisionedTPS - Specifies the requested minimum provisioned transactions" +
                "(recommendations) per second that Amazon Personalize will support.\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String campaignArn = args[0];
        String solutionVersionArn = args[1];
        Integer minProvisionedTPS = Integer.parseInt(args[2]);


        //Change the region to the region where your resources are located.
        Region region = Region.US_WEST_2;

        // Build a personalize client
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        updateCampaign(personalizeClient, campaignArn, solutionVersionArn, minProvisionedTPS);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.update_campaign.main]
    public static String updateCampaign(PersonalizeClient personalizeClient,
                                      String campaignArn,
                                      String solutionVersionArn,
                                      Integer minProvisionedTPS) {

        try {
            // build the updateCampaignRequest
            UpdateCampaignRequest updateCampaignRequest = UpdateCampaignRequest.builder()
                    .campaignArn(campaignArn)
                    .solutionVersionArn(solutionVersionArn)
                    .minProvisionedTPS(minProvisionedTPS)
                    .build();

            // update the campaign
            personalizeClient.updateCampaign(updateCampaignRequest);

            DescribeCampaignRequest campaignRequest = DescribeCampaignRequest.builder()
                    .campaignArn(campaignArn)
                    .build();

            DescribeCampaignResponse campaignResponse = personalizeClient.describeCampaign(campaignRequest);
            Campaign updatedCampaign = campaignResponse.campaign();

            System.out.println("The Campaign status is " + updatedCampaign.status());
            return updatedCampaign.status();

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    //snippet-end:[personalize.java2.update_campaign.main]

}