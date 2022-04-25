/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.delete_dataset.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.util.logging.Level;
import java.util.logging.Logger;

// Deletes an Amazon Lookout for Vision dataset.
public class DeleteDataset {

    public static final Logger logger = Logger.getLogger(DeleteDataset.class.getName());

    public static void main(String args[]) throws Exception {

        String projectName = null;
        String datasetType = null;

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteDataset <project_name> <dataset_type>  \n\n" +
                "Where:\n" +
                "    project_name - The name of the project that contains the dataset that you want to delete.\n\n" +
                "    dataset_type - The type of the dataset that you want to delete.\n\n";

        try {

            if (args.length != 2) {
                System.out.println(USAGE);
                System.exit(1);
            }

            projectName = args[0];
            datasetType = args[1];

            // Get the Lookout for Vision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            System.out.printf("Deleting %s dataset for project %s%n", datasetType, projectName);
            Datasets.deleteDataset(lfvClient, projectName, datasetType);
            System.out.printf("Deleted %s dataset for project %s%n", datasetType, projectName);

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not delete dataset: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not delete dataset: %s", lfvError.getMessage()));
            System.exit(1);
        }

    }

}
// snippet-end:[lookoutvision.java2.delete_dataset.complete]
