//snippet-sourcedescription:[CreateRecommender.java demonstrates how to create an Amazon Personalize recommender
// for a domain dataset group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[1/3/2022]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_recommender.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateRecommenderRequest;
import software.amazon.awssdk.services.personalize.model.CreateRecommenderResponse;
import software.amazon.awssdk.services.personalize.model.DescribeRecommenderRequest;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

import java.time.Instant;
//snippet-end:[personalize.java2.create_recommender.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateRecommender {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateRecommender <solutionVersionArn> <name>\n\n" +
                "Where:\n" +
                "    datasetGroupArn - The ARN of the destination dataset group for the recommender.\n" +
                "    name - The name of the new recommender.\n" +
                "    recipeArn - The ARN of the use case (recipe) for the recommender.\n\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String datasetGroupArn = args[0];
        String name = args[1];
        String recipeArn = args[2];

        // Change to the region where your resources are located
        Region region = Region.US_WEST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        createRecommender(personalizeClient, datasetGroupArn, name, recipeArn);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.create_recommender.main]
    public static String createRecommender(PersonalizeClient personalizeClient,
                                           String name,
                                           String datasetGroupArn,
                                           String recipeArn) {

        long maxTime = 0;
        long waitInMilliseconds = 30 * 1000; // 30 seconds
        String recommenderStatus = "";

        try {
                CreateRecommenderRequest createRecommenderRequest = CreateRecommenderRequest.builder()
                        .datasetGroupArn(datasetGroupArn)
                        .name(name)
                        .recipeArn(recipeArn)
                        .build();

                CreateRecommenderResponse recommenderResponse = personalizeClient.createRecommender(createRecommenderRequest);
                String recommenderArn = recommenderResponse.recommenderArn();
                System.out.println("The recommender ARN is " + recommenderArn);

                DescribeRecommenderRequest describeRecommenderRequest = DescribeRecommenderRequest.builder()
                        .recommenderArn(recommenderArn)
                        .build();

                maxTime = Instant.now().getEpochSecond() + 3 * 60 * 60;

                while (Instant.now().getEpochSecond() < maxTime) {

                    recommenderStatus = personalizeClient.describeRecommender(describeRecommenderRequest).recommender().status();
                    System.out.println("Recommender status: " + recommenderStatus);

                    if (recommenderStatus.equals("ACTIVE") || recommenderStatus.equals("CREATE FAILED")) {
                        break;
                    }
                    try {
                        Thread.sleep(waitInMilliseconds);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
                return recommenderArn;

        } catch(PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    //snippet-end:[personalize.java2.create_recommender.main]
}
