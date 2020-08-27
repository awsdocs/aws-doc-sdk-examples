//snippet-sourcedescription:[CreateSolution.java demonstrates how to create an Amazon Personalize solution.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Personalize]
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

//snippet-start:[personalize.java2.create_solution.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateSolutionRequest;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.CreateSolutionResponse;
//snippet-end:[personalize.java2.create_solution.import]

public class CreateSolution {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateSolution <datasetGroupArn> <solutionName><recipeArn>\n\n" +
                "Where:\n" +
                "    datasetGroupArn - The Amazon Resource Name (ARN) of the dataset group.\n\n" +
                "    solutionName - The name of the Amazon Personalize campaign.\n\n" +
                "    recipeArn - The ARN of the recipe.\n\n" ;

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String datasetGroupArn = args[0];
        String solutionName = args[1];
        String recipeArn = args[2];

        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        String solutionArn = createPersonalizeSolution(personalizeClient, datasetGroupArn, solutionName, recipeArn);
        System.out.println("The Amazon Personalize solution ARN is "+solutionArn);
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
