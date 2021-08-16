//snippet-sourcedescription:[CreateDatasetGroup.java demonstrates how to create an Amazon Personalize dataset group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[5/11/2021]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_dataset_group.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.CreateDatasetGroupRequest;
import software.amazon.awssdk.services.personalize.model.DescribeDatasetGroupRequest;

import java.time.Instant;
//snippet-end:[personalize.java2.create_dataset_group.import]

public class CreateDatasetGroup {

    public static void main(String[] args) {

        final String USAGE = "Usage:\n" +
                "    CreateDatasetGroup <name>\n\n" +
                "Where:\n" +
                "   name - The name for the new dataset group.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Change to the region where your resources are located
        Region region = Region.US_WEST_2;

        String datasetGroupName = args[0];

        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        String datasetGroupArn = createDatasetGroup(personalizeClient, datasetGroupName);
        System.out.println("Dataset group ARN: " + datasetGroupArn);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.create_dataset_group.main]
    public static String createDatasetGroup(PersonalizeClient personalizeClient, String datasetGroupName) {

        try {
            CreateDatasetGroupRequest createDatasetGroupRequest = CreateDatasetGroupRequest.builder()
                    .name(datasetGroupName)
                    .build();
            return personalizeClient.createDatasetGroup(createDatasetGroupRequest).datasetGroupArn();
        } catch (PersonalizeException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }
    //snippet-end:[personalize.java2.create_dataset_group.main]

}
