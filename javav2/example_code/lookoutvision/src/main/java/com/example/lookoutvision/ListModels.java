/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.list_models.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelMetadata;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// List the models in an Amazon Lookout for Vision project.
public class ListModels {

    public static final Logger logger = Logger.getLogger(ListModels.class.getName());

    public static void main(String[] args) {

        String projectName = null;

        final String USAGE = "\n" + "Usage: " + "<project_name>\n\n"
                + "Where:\n"
                + "   project_arn - The project that contains the models that you want to list.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];

        try {

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Get a list of models in the supplied project.
            List<ModelMetadata> models = Models.listModels(lfvClient, projectName);

            System.out.println(String.format("Project: %s", projectName));

            for (ModelMetadata model : models) {
                // Show the model details.

                System.out.println(String.format("Version: %s", model.modelVersion()));
                System.out.println(String.format("ARN: %s", model.modelArn()));
                System.out.println(String.format("Status: %s", model.statusAsString()));
                System.out.println(String.format("Status message: %s", model.statusMessage()));

                if (model.performance() != null) {
                    System.out.println("Testing results");
                    System.out.println(String.format("F1: %s", model.performance().f1Score().toString()));
                    System.out.println(String.format("Precision: %s", model.performance().precision().toString()));
                    System.out.println(String.format("recall: %s", model.performance().recall().toString()));

                }
                System.out.println();
            }

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not list models: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not list models: %s", lfvError.getMessage()));
            System.exit(1);
        }
    }

}
// snippet-end:[lookoutvision.java2.list_models.complete]