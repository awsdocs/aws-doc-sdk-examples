//snippet-sourcedescription:[CreateCampaign.java demonstrates how to create an Amazon Personalize campaign.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Amazon Personalize]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/21/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.personalize;

//snippet-start:[personalize.java2.create_campaign.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateCampaignRequest;
import software.amazon.awssdk.services.personalize.model.CreateCampaignResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
//snippet-end:[personalize.java2.create_campaign.import]

public class CreateCampaign {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateCampaign <solutionVersionArn> <name>\n\n" +
                "Where:\n" +
                "    solutionVersionArn - The ARN of the solution version.\n\n" +
                "    name - The name of the Amazon Personalization campaign\n\n" ;

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String solutionVersionArn = args[0];
        String name = args[1];

        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        createPersonalCompaign(personalizeClient, solutionVersionArn, name);
    }

    //snippet-start:[personalize.java2.create_campaign.main]
    public static void createPersonalCompaign(PersonalizeClient personalizeClient, String solutionVersionArn, String name) {

        try {

            CreateCampaignRequest createCampaignRequest = CreateCampaignRequest.builder()
                .minProvisionedTPS(1)
                .solutionVersionArn(solutionVersionArn)
                .name(name)
                .build();

            CreateCampaignResponse campaignResponse = personalizeClient.createCampaign(createCampaignRequest);
            System.out.println("The Campaign ARN is "+campaignResponse.campaignArn());

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.create_campaign.main]
}
