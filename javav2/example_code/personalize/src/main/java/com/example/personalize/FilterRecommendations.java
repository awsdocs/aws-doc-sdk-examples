//snippet-sourcedescription:[FilterRecommendations.java demonstrates how use a filter when requesting recommendations]
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

//snippet-start:[personalize.java2.filter_recommendations.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalizeruntime.model.PersonalizeRuntimeException;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsRequest;
import software.amazon.awssdk.services.personalizeruntime.model.GetRecommendationsResponse;
import software.amazon.awssdk.services.personalizeruntime.model.PredictedItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//snippet-end:[personalize.java2.filter_recommendations.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class FilterRecommendations {

    public static void main(String[] args) {

        // This example shows how to use a filter with an expression that has
        // two placeholder parameters, and passes two values to the first and one to the second.
        // in the GetRecommendations request.
        // Your filter may not have the same number of parameters or you may not want to use two values.
        // Change the following code and the PersonalizeTest.java code based on the number of parameters
        // your expression uses and the values you want to pass.

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetRecommendations <campaignArn> <userId> <filterArn> <parameter1Name> " +
                "       <parameter1Value1> <parameter1Value2> <parameter2Name> <parameter2Value>\n\n" +
                "Where:\n" +
                "    campaignArn - The Amazon Resource Name (ARN) of the campaign.\n\n" +
                "    userId - The user ID to provide recommendations for." +
                "    filterArn - The ARN of the filter to use." +
                "    parameter1Name - The name of the first placeholder parameter in the filter." +
                "    parameter1Value1 - The first value to pass to the first parameter." +
                "    parameter1Value2 - The second value to pass to the first parameter." +
                "    parameter2Name = The name of the second placeholder parameter in the filter." +
                "    parameter2Value = The value to pass to the second parameter\n\n";

        if (args.length != 8) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String campaignArn = args[0];
        String userId = args[1];
        String filterArn = args[2];
        String parameter1Name = args[3];
        String parameter1Value1 = args[4];
        String parameter1Value2 = args[5];
        String parameter2Name = args[6];
        String parameter2Value = args[7];

        Region region = Region.US_EAST_1;
        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();

        getFilteredRecs(personalizeRuntimeClient, campaignArn, userId, filterArn, parameter1Name, parameter1Value1,
                parameter1Value2, parameter2Name, parameter2Value);
        personalizeRuntimeClient.close();
    }
    //snippet-start:[personalize.java2.filter_recommendations.main]
    public static void getFilteredRecs(PersonalizeRuntimeClient personalizeRuntimeClient,
                                       String campaignArn,
                                       String userId,
                                       String filterArn,
                                       String parameter1Name,
                                       String parameter1Value1,
                                       String parameter1Value2,
                                       String parameter2Name,
                                       String parameter2Value){

        try {

            Map<String, String> filterValues = new HashMap<>();

            filterValues.put(parameter1Name, String.format("\"%1$s\",\"%2$s\"",
                    parameter1Value1, parameter1Value2));
            filterValues.put(parameter2Name, String.format("\"%1$s\"",
                    parameter2Value));

            GetRecommendationsRequest recommendationsRequest = GetRecommendationsRequest.builder()
                    .campaignArn(campaignArn)
                    .numResults(20)
                    .userId(userId)
                    .filterArn(filterArn)
                    .filterValues(filterValues)
                    .build();

            GetRecommendationsResponse recommendationsResponse = personalizeRuntimeClient.getRecommendations(recommendationsRequest);
            List<PredictedItem> items = recommendationsResponse.itemList();

            for (PredictedItem item: items) {
                System.out.println("Item Id is : "+item.itemId());
                System.out.println("Item score is : "+item.score());
            }
        } catch (PersonalizeRuntimeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.filter_recommendations.main]
}