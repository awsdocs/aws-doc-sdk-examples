// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.personalize;

// snippet-start:[personalize.java2.create_dataset.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.model.CreateDatasetRequest;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
// snippet-end:[personalize.java2.create_dataset.import]

public class CreateDataset {
    public static void main(String[] args) {

        final String USAGE = """
                Usage:
                    <datasetName> <datasetGroupArn> <datasetType> <schemaArn>

                Where:
                   datasetName - The name for the dataset.
                   datasetGroupArn - The Amazon Resource Name (ARN) for the dataset group.
                   datasetType- The type of dataset (INTERACTIONS, USERS, or ITEMS).
                   schemaArn - The ARN for the dataset's schema.

                """;

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

    // snippet-start:[personalize.java2.create_dataset.main]
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
    // snippet-end:[personalize.java2.create_dataset.main]
}
