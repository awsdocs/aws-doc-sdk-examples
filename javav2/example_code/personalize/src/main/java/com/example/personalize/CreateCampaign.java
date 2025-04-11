// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.personalize;

// snippet-start:[personalize.java2.create_campaign.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateCampaignRequest;
import software.amazon.awssdk.services.personalize.model.CreateCampaignResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
// snippet-end:[personalize.java2.create_campaign.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development
 * environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateCampaign {

    public static void main(String[] args) {

        final String USAGE = """

                Usage:
                    <solutionVersionArn> <name>

                Where:
                    solutionVersionArn - The ARN of the solution version.

                    name - The name of the Amazon Personalization campaign.

                """;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String solutionVersionArn = args[0];
        String name = args[1];
        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        createPersonalCompaign(personalizeClient, solutionVersionArn, name);
        personalizeClient.close();
    }

    // snippet-start:[personalize.java2.create_campaign.main]
    public static String createPersonalCompaign(PersonalizeClient personalizeClient, String solutionVersionArn,
            String name) {

        try {
            CreateCampaignRequest createCampaignRequest = CreateCampaignRequest.builder()
                    .minProvisionedTPS(1)
                    .solutionVersionArn(solutionVersionArn)
                    .name(name)
                    .build();

            CreateCampaignResponse campaignResponse = personalizeClient.createCampaign(createCampaignRequest);
            System.out.println("The campaign ARN is " + campaignResponse.campaignArn());
            return campaignResponse.campaignArn();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    // snippet-end:[personalize.java2.create_campaign.main]
}
