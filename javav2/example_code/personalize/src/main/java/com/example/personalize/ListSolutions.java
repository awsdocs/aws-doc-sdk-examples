//snippet-sourcedescription:[ListSolutions.java demonstrates how to list Amazon Personalize solutions.]
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

//snippet-start:[personalize.java2.list_solutions.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.ListSolutionsRequest;
import software.amazon.awssdk.services.personalize.model.ListSolutionsResponse;
import software.amazon.awssdk.services.personalize.model.SolutionSummary;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import java.util.List;
//snippet-end:[personalize.java2.list_solutions.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListSolutions {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListSolutions <datasetGroupArn>\n\n" +
                "Where:\n" +
                "    datasetGroupArn - The ARN of the data set group.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String datasetGroupArn = args[0];
        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        listAllSolutions(personalizeClient, datasetGroupArn);
        personalizeClient.close();
    }
//snippet-start:[personalize.java2.list_solutions.main]

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
//snippet-end:[personalize.java2.list_solutions.main]
