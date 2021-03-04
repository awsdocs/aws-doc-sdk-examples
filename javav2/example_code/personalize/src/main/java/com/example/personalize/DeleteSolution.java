//snippet-sourcedescription:[DeleteSolution.java demonstrates how to delete an Amazon Personalize solution.]
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

//snippet-start:[personalize.java2.delete_solution.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.DeleteSolutionRequest;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
//snippet-end:[personalize.java2.delete_solution.import]

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
