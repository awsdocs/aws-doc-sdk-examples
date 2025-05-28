// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.personalize;

// snippet-start:[personalize.java2.delete_campaign.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.DeleteCampaignRequest;
import software.amazon.awssdk.services.personalize.model.DescribeCampaignRequest;
import software.amazon.awssdk.services.personalize.model.DescribeCampaignResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
// snippet-end:[personalize.java2.delete_campaign.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development
 * environment, including your credentials.
 * <p>
 * For information, see this documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteCampaign {

    public static void main(String[] args) {
        final String USAGE = """
                Usage:
                    DeleteCampaign <campaignArn>
                
                Where:
                    campaignArn - The ARN of the campaign to delete.
                """;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String campaignArn = args[0];
        Region region = Region.US_EAST_1;
        try (PersonalizeClient personalizeClient = PersonalizeClient.builder().region(region).build()) {
            waitForCampaignToBeDeletable(personalizeClient, campaignArn);
            deleteSpecificCampaign(personalizeClient, campaignArn);
            System.out.println("Campaign deleted successfully: " + campaignArn);
        }
    }

    // Polls until the campaign is in a deletable state
    public static void waitForCampaignToBeDeletable(PersonalizeClient personalizeClient, String campaignArn) {
        try {
            while (true) {
                DescribeCampaignRequest describeRequest = DescribeCampaignRequest.builder()
                        .campaignArn(campaignArn)
                        .build();

                DescribeCampaignResponse describeResponse = personalizeClient.describeCampaign(describeRequest);
                String status = describeResponse.campaign().status();
                System.out.println("Current campaign status: " + status);

                // Check if it's in a deletable state (using string values)
                if (status.equalsIgnoreCase("ACTIVE") ||
                        status.equalsIgnoreCase("CREATE FAILED") ||
                        status.equalsIgnoreCase("STOPPED")) {
                    return; // Campaign is now deletable
                }

                // If it's in DELETE PENDING, it means it's already being deleted
                if (status.equalsIgnoreCase("DELETE PENDING")) {
                    throw new RuntimeException("Campaign is already being deleted.");
                }

                // Wait before checking again to avoid excessive API calls
                Thread.sleep(10_000);
            }
        } catch (PersonalizeException e) {
            System.err.println("Error while polling campaign state: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for campaign to be deletable.", e);
        }
    }

    // snippet-start:[personalize.java2.delete_campaign.main]
    public static void deleteSpecificCampaign(PersonalizeClient personalizeClient, String campaignArn) {
        try {
            DeleteCampaignRequest campaignRequest = DeleteCampaignRequest.builder()
                    .campaignArn(campaignArn)
                    .build();

            personalizeClient.deleteCampaign(campaignRequest);
            System.out.println("Delete request sent successfully.");
        } catch (PersonalizeException e) {
            System.err.println("Error deleting campaign: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException(e);
        }
    }
}
// snippet-end:[personalize.java2.delete_campaign.main]

