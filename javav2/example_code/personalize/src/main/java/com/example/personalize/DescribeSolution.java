//snippet-sourcedescription:[DescribeSolution.java demonstrates how to describe an Amazon Personalize solution.]
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

//snippet-start:[personalize.java2.describe_solution.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.DescribeSolutionRequest;
import software.amazon.awssdk.services.personalize.model.DescribeSolutionResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
//snippet-end:[personalize.java2.describe_solution.import]

public class DescribeSolution {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DescribeSolution <solutionArn>\n\n" +
                "Where:\n" +
                "    solutionArn - The ARN of the solution.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String solutionArn = args[0];

        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        describeSpecificSolution(personalizeClient, solutionArn);
    }

    //snippet-start:[personalize.java2.describe_solution.main]
   public static void describeSpecificSolution(PersonalizeClient personalizeClient, String solutionArn) {

     try {
        DescribeSolutionRequest solutionRequest = DescribeSolutionRequest.builder()
                .solutionArn(solutionArn)
                .build();

        DescribeSolutionResponse response = personalizeClient.describeSolution(solutionRequest);
        System.out.println("The Solution name is "+response.solution().name());

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.describe_solution.main]
}
