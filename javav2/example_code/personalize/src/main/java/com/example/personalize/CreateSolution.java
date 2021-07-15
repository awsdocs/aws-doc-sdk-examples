//snippet-sourcedescription:[CreateSolution.java demonstrates how to create an Amazon Personalize solution.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2020]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_solution.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateSolutionRequest;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.CreateSolutionResponse;
//snippet-end:[personalize.java2.create_solution.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateSolution {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateSolution <datasetGroupArn> <solutionName> <recipeArn>\n\n" +
                "Where:\n" +
                "    datasetGroupArn - The ARN of the dataset group.\n\n" +
                "    solutionName - The name of the Amazon Personalization campaign.\n\n" +
                "    recipeArn - The ARN of the recipe.\n\n" ;

        if (args.length != 3) {
           System.out.println(USAGE);
            System.exit(1);
        }

        String datasetGroupArn = args[0];
        String solutionName = args[1];
        String recipeArn = args[2];

        // Change to the region where your resources are located
        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        String solutionArn = createPersonalizeSolution(personalizeClient, datasetGroupArn, solutionName, recipeArn);
        System.out.println("The Amazon Personalize solution ARN is "+solutionArn);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.create_solution.main]
    public static String createPersonalizeSolution(PersonalizeClient personalizeClient,
                                                 String datasetGroupArn,
                                                 String solutionName,
                                                 String recipeArn) {

        try {
            CreateSolutionRequest solutionRequest = CreateSolutionRequest.builder()
                .name(solutionName)
                .datasetGroupArn(datasetGroupArn)
                .recipeArn(recipeArn)
                .build();

            CreateSolutionResponse solutionResponse = personalizeClient.createSolution(solutionRequest);
            return solutionResponse.solutionArn();

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
 }
    //snippet-end:[personalize.java2.create_solution.main]
}
