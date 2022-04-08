/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.describe_dataset.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.DatasetDescription;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.util.logging.Level;
import java.util.logging.Logger;

// Describes an Amazon Lookout for Vision dataset.
public class DescribeDataset {

    public static final Logger logger = Logger.getLogger(DescribeDataset.class.getName());

    public static void main(String[] args) throws Exception {

        String projectName = null;
        String datasetType = null;

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DescribeProject <project_name> <dataset_type> \n\n" +
                "Where:\n" +
                "    project_name - the name of the project that you want to describe.\n" +
                "    dataset_type - the type (train or test) of the dataset that you want to describe.\n\n";

        try {

            if (args.length != 2) {
                System.out.println(USAGE);
                System.exit(1);
            }

            projectName = args[0];
            datasetType = args[1];

            // Get the Lookout for Vision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            System.out.printf("Describing project %s%n", projectName, datasetType);

            DatasetDescription datasetDescription = Datasets.describeDataset(lfvClient, projectName, datasetType);

            String description = String.format("Project: %s%n"
                    + "Created: %s%n"
                    + "Type: %s%n"
                    + "Total: %s%n"
                    + "Labeled: %s%n"
                    + "Normal: %s%n"
                    + "Anomalous: %s%n",
                    datasetDescription.projectName(),
                    datasetDescription.creationTimestamp(),
                    datasetDescription.datasetType(),
                    datasetDescription.imageStats().total().toString(),
                    datasetDescription.imageStats().labeled().toString(),
                    datasetDescription.imageStats().normal().toString(),
                    datasetDescription.imageStats().anomaly().toString());

            System.out.println(description);

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not describe dataset: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not describe dataset: %s", lfvError.getMessage()));
            System.exit(1);
        }

    }

}
// snippet-end:[lookoutvision.java2.describe_dataset.complete]
