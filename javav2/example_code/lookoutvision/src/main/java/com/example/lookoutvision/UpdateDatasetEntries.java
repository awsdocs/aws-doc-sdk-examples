/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.update_dataset_entries.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.DatasetStatus;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

// Updates dataset entries in an Amazon Lookout for Vision dataset.
public class UpdateDatasetEntries {

    public static final Logger logger = Logger.getLogger(UpdateDatasetEntries.class.getName());

    public static void main(String[] args) throws Exception {

        String projectName = null;
        String datasetType = null;
        String updateFile = null;

        final String USAGE = "\n" +
                "Usage:\n" +
                "    UpdateDatasetEntries <project_name> <dataset_type> <manifest_file> \n\n" +
                "Where:\n" +
                "    project_name - the name of the project that you want to use.\n" +
                "    dataset_type - the type (train or test) of the dataset that you want to update.\n" +
                "    manifest_file - the manifest file that you want to use.\n\n";

        try {

            if (args.length != 3) {
                System.out.println(USAGE);
                System.exit(1);
            }

            projectName = args[0];
            datasetType = args[1];
            updateFile = args[2];

            // Get the Lookout for Vision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Update dataset from manifest file.

            System.out.printf("Updating %s dataset for  project %s%n", datasetType, projectName);

            DatasetStatus status = Datasets.updateDatasetEntries(lfvClient, projectName, datasetType, updateFile);

            if (status == DatasetStatus.CREATE_COMPLETE) {
                System.out.printf("Updated %s dataset for project %s%n", datasetType, projectName);
            } else {
                System.out.printf("Updated failed for %s dataset in project %s: %s", datasetType, projectName,
                        status.toString());

            }

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not update dataset entriesl: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not update dataset entries: %s", lfvError.getMessage()));
            System.exit(1);
        } catch (FileNotFoundException fileErr) {
            logger.log(Level.SEVERE, "File not found: {0}", fileErr.getMessage());
            System.out.println(String.format("File not found: %s", fileErr.getMessage()));
            System.exit(1);

        }

    }

}
// snippet-end:[lookoutvision.java2.update_dataset_entries.complete]
