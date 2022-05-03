/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.create_dataset.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.DatasetDescription;
import software.amazon.awssdk.services.lookoutvision.model.DatasetStatus;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.util.logging.Level;
import java.util.logging.Logger;

// Creates an Amazon Lookout for Vision dataset.
public class CreateDataset {

    public static final Logger logger = Logger.getLogger(CreateDataset.class.getName());

    public static void main(String[] args) {

        String datasetType = null;
        String bucket = null;
        String manifestFile = null;
        String projectName = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <dataset_type> <bucket> <manifest_file>\n\n"
                + "Where:\n"
                + "   project_arn - The ARN of the project that you want to add the dataset to.\n\n"
                + "   dataset_type - The type of the dataset that you want to create (train or test).\n\n"
                + "   bucket - The S3 bucket that contains the manifest file.\n\n"
                + "   manifest_file - The location and name of the manifest file within the bucket.\n\n";

        switch (args.length) {
            case 2:
                projectName = args[0];
                datasetType = args[1];
                break;

            case 4:
                projectName = args[0];
                datasetType = args[1];
                bucket = args[2];
                manifestFile = args[3];
                break;

            default:
                // invalid number of arguments.
                System.out.println(USAGE);

        }
        try {

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Create the dataset.
            DatasetDescription datasetDescription = Datasets.createDataset(lfvClient, projectName, datasetType, bucket,
                    manifestFile);

            if (datasetDescription.status() == DatasetStatus.CREATE_COMPLETE) {
                System.out.println(String.format("Created dataset: %s for project: %s", datasetType, projectName));
            } else {
                System.out.println("Error creating dataset: " + datasetDescription.statusMessage());
            }

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not create dataset: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not create dataset: %s", lfvError.getMessage()));
            System.exit(1);
        } catch (InterruptedException intError) {
            logger.log(Level.SEVERE, "Interrupt exception occurred: {0}", intError.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
// snippet-end:[lookoutvision.java2.create_dataset.complete]