/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.delete_dataset.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

// Deletes an Amazon Lookout for Vision dataset.
public class DeleteModel {

    public static final Logger logger = Logger.getLogger(DeleteDataset.class.getName());

    public static void main(String args[]) throws Exception {

        String projectName = null;
        String modelVersion = null;

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteDataset <project_name> <dataset_type>  \n\n" +
                "Where:\n" +
                "    project_name - The name of the project that contains the model that you want to delete.\n\n" +
                "    model_version - The version of the model that you want to delete.\n\n";

        try {

            if (args.length != 2) {
                System.out.println(USAGE);
                System.exit(1);
            }

            projectName = args[0];
            modelVersion = args[1];

            System.out.printf(
                    "You are charged each time you successfully train a model.%n"
                    +"Are you sure you want to delete model version %s in project %s?%n"
                    +"Enter YES to delete the model.%n",
                    modelVersion, projectName);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));

            // Reading data using readLine
            String confirmDelete = reader.readLine();

            if (confirmDelete.equals("YES")) {

                // Get the Lookout for Vision client.
                LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

                System.out.printf("Deleting model version %s in project %s%n", modelVersion, projectName);
                Models.deleteModel(lfvClient, projectName, modelVersion);
                System.out.printf("Deleted model version %s in project %s%n", modelVersion, projectName);
            }
            else{
                System.out.printf("Request cancelled. Not deleting model version %s in project %s%n", modelVersion, projectName);
            }

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
