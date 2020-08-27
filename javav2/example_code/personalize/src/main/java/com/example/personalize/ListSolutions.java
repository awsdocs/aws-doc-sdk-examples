//snippet-sourcedescription:[ListSolutions.java demonstrates how to list Amazon Personalize solutions.]
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

//snippet-start:[personalize.java2.list_solutions.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.ListSolutionsRequest;
import software.amazon.awssdk.services.personalize.model.ListSolutionsResponse;
import software.amazon.awssdk.services.personalize.model.SolutionSummary;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

import java.util.List;
//snippet-end:[personalize.java2.list_solutions.import]

public class ListSolutions {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListSolutions <datasetGroupArn>\n\n" +
                "Where:\n" +
                "    datasetGroupArn - The Amazon Resource Name (ARN) of the dataset group.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String datasetGroupArn = args[0];


        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        listAllSolutions(personalizeClient, datasetGroupArn);
    }

    public static void listAllSolutions(PersonalizeClient personalizeClient, String datasetGroupArn) {

        try {
        ListSolutionsRequest solutionsRequest = ListSolutionsRequest.builder()
                .maxResults(10)
                .datasetGroupArn(datasetGroupArn)
                .build() ;

        ListSolutionsResponse response = personalizeClient.listSolutions(solutionsRequest);
        List<SolutionSummary> solutions = response.solutions();
        for (SolutionSummary solution: solutions) {
            System.out.println("The solution ARN is: "+solution.solutionArn());
            System.out.println("The solution name is: "+solution.name());
        }

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
