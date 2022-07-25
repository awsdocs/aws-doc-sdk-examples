//snippet-sourcedescription:[DeleteCampaign.java demonstrates how to delete an Amazon Personalize campaign.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Personalize]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_campaign.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.DeleteCampaignRequest;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
//snippet-end:[personalize.java2.create_campaign.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteCampaign {

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "Usage:\n" +
            "    DeleteCampaign <campaignArn> \n\n" +
            "Where:\n" +
            "    campaignArn - The ARN of the campaign to delete.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String campaignArn = args[0];
        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
            .region(region)
            .build();

        deleteSpecificCampaign(personalizeClient, campaignArn) ;
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.create_campaign.main]
    public static void deleteSpecificCampaign(PersonalizeClient personalizeClient, String campaignArn ) {

        try {
            DeleteCampaignRequest campaignRequest = DeleteCampaignRequest.builder()
                .campaignArn(campaignArn)
                .build();

            personalizeClient.deleteCampaign(campaignRequest);

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.create_campaign.main]
}
