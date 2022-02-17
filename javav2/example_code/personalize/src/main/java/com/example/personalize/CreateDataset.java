//snippet-sourcedescription:[CreateDataset.java demonstrates how to create an Amazon Personalize dataset.]
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

//snippet-start:[personalize.java2.create_dataset.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.model.CreateDatasetRequest;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
//snippet-end:[personalize.java2.create_dataset.import]

public class CreateDataset {

    public static void main(String[] args) {

        final String USAGE = "Usage:\n" +
                "    CreateDataset <datasetName, dataset group arn, dataset type, schema arn>\n\n" +
                "Where:\n" +
                "   name - The name for the dataset.\n" +
                "   dataset group arn - The Amazon Resource Name (ARN) for the dataset group.\n" +
                "   dataset type - The type of dataset (INTERACTIONS, USERS, or ITEMS).\n" +
                "   schema arn - The ARN for the dataset's schema.\n\n";

        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String datasetName = args[0];
        String datasetGroupArn = args[1];
        String datasetType = args[2];
        String schemaArn = args[3];

        // Change to the region where your resources are located
        Region region = Region.US_WEST_2;

        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        String datasetArn = createDataset(personalizeClient, datasetName, datasetGroupArn, datasetType, schemaArn);
        System.out.println("Dataset ARN: " + datasetArn);
        personalizeClient.close();

    }

    //snippet-start:[personalize.java2.create_dataset.main]
    public static String createDataset(PersonalizeClient personalizeClient,
                                       String datasetName,
                                       String datasetGroupArn,
                                       String datasetType,
                                       String schemaArn) {
        try {
            CreateDatasetRequest request = CreateDatasetRequest.builder()
                    .name(datasetName)
                    .datasetGroupArn(datasetGroupArn)
                    .datasetType(datasetType)
                    .schemaArn(schemaArn)
                    .build();

            String datasetArn = personalizeClient.createDataset(request)
                    .datasetArn();
            System.out.println("Dataset " + datasetName + " created.");
            return datasetArn;
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    //snippet-end:[personalize.java2.create_dataset.main]


}