//snippet-sourcedescription:[GetRecommendations.java demonstrates how to return a list of recommended items.]
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

//snippet-start:[personalize.java2.get_recommendations.import]
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsRequest;
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsResponse;
import software.amazon.awssdk.services.personalizeruntime.model.PredictedItem;
import java.util.List;
//snippet-end:[personalize.java2.get_recommendations.import]

public class GetRecommendations {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetRecommendations <campaignArn> <userId>\n\n" +
                "Where:\n" +
                "    campaignArn - The ARN of the campaign.\n\n" +
                "    userId - The user ID to provide recommendations for\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String campaignArn = args[0];
        String userId = args[1];

        Region region = Region.US_EAST_1;
        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();

        getRecs(personalizeRuntimeClient, campaignArn, userId);
    }
    //snippet-start:[personalize.java2.get_recommendations.main]
    public static void getRecs(PersonalizeRuntimeClient personalizeRuntimeClient, String campaignArn, String userId){

        try {
            GetRecommendationsRequest recommendationsRequest = GetRecommendationsRequest.builder()
                .campaignArn(campaignArn)
                .numResults(20)
                .userId(userId)
                .build();

            GetRecommendationsResponse recommendationsResponse = personalizeRuntimeClient.getRecommendations(recommendationsRequest);
            List<PredictedItem> items = recommendationsResponse.itemList();

            for (PredictedItem item: items) {
                System.out.println("Item Id is : "+item.itemId());
                System.out.println("Item score is : "+item.score());
            }
        } catch (AwsServiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.get_recommendations.main]
}