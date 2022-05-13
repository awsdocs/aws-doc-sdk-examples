//snippet-sourcedescription:[CreateSolutionVersion.java demonstrates how to create
// an Amazon Personalize solution version (train a model).]
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

//snippet-start:[personalize.java2.create_solution_version.import]
import java.time.Instant;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateSolutionVersionRequest;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.CreateSolutionVersionResponse;
import software.amazon.awssdk.services.personalize.model.DescribeSolutionRequest;
import software.amazon.awssdk.services.personalize.model.DescribeSolutionVersionRequest;
//snippet-end:[personalize.java2.create_solution_version.import]

public class CreateSolutionVersion {

    public static void main(String [] args) {

    
    final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateSolutionVersion <solutionArn>\n\n" +
                "Where:\n" +
                "    solutionArn - The ARN of the solution.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String solutionArn = args[0];

        Region region = Region.US_WEST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        String solutionVersionArn = createPersonalizeSolutionVersion(personalizeClient, solutionArn);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.create_solution_version.main]
    public static String createPersonalizeSolutionVersion(PersonalizeClient personalizeClient, String solutionArn) {
        long maxTime = 0;
        long waitInMilliseconds = 30 * 1000; // 30 seconds
        String solutionStatus = "";
        String solutionVersionStatus = "";
        String solutionVersionArn = "";

        try {
            DescribeSolutionRequest describeSolutionRequest = DescribeSolutionRequest.builder()
                .solutionArn(solutionArn)
                .build();
            
            maxTime = Instant.now().getEpochSecond() + 3 * 60 * 60;

            // Wait until solution is active. 
            while (Instant.now().getEpochSecond() < maxTime) {

                solutionStatus = personalizeClient.describeSolution(describeSolutionRequest).solution().status();
                System.out.println("Solution status: " + solutionStatus);

                if (solutionStatus.equals("ACTIVE") || solutionStatus.equals("CREATE FAILED")) {
                    break;
                }
                try {
                    Thread.sleep(waitInMilliseconds);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (solutionStatus.equals("ACTIVE")) {

                CreateSolutionVersionRequest createSolutionVersionRequest = CreateSolutionVersionRequest.builder()
                    .solutionArn(solutionArn)
                    .build();

                CreateSolutionVersionResponse createSolutionVersionResponse = personalizeClient.createSolutionVersion(createSolutionVersionRequest);
                solutionVersionArn = createSolutionVersionResponse.solutionVersionArn();

                System.out.println("Solution version ARN: " + solutionVersionArn);

                DescribeSolutionVersionRequest describeSolutionVersionRequest = DescribeSolutionVersionRequest.builder()
                    .solutionVersionArn(solutionVersionArn)
                    .build();

                while (Instant.now().getEpochSecond() < maxTime) {

                    solutionVersionStatus = personalizeClient.describeSolutionVersion(describeSolutionVersionRequest).solutionVersion().status();
                    System.out.println("Solution version status: " + solutionVersionStatus);

                    if (solutionVersionStatus.equals("ACTIVE") || solutionVersionStatus.equals("CREATE FAILED")) {
                        break;
                    }
                    try {
                        Thread.sleep(waitInMilliseconds);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
                return solutionVersionArn;
            }
        } catch(PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    //snippet-end:[personalize.java2.create_solution_version.main]
}
