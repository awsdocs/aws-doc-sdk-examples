//snippet-sourcedescription:[DeleteSolution.java demonstrates how to delete an Amazon Personalize solution.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Personalize]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.delete_solution.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.DeleteSolutionRequest;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
//snippet-end:[personalize.java2.delete_solution.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteSolution {

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "Usage:\n" +
            "    DeleteSolution <solutionArn>\n\n" +
            "Where:\n" +
            "    solutionArn - the ARN of the solution to delete.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String solutionArn = args[0];
        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
            .region(region)
            .build();

        deleteGivenSolution(personalizeClient, solutionArn );
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.delete_solution.main]
    public static void deleteGivenSolution(PersonalizeClient personalizeClient, String solutionArn ) {

        try {
            DeleteSolutionRequest solutionRequest = DeleteSolutionRequest.builder()
                .solutionArn(solutionArn)
                .build();

            personalizeClient.deleteSolution(solutionRequest);
            System.out.println("Done");

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.delete_solution.main]
}
