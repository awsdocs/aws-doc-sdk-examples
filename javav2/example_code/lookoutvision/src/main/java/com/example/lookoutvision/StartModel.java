/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.start_model.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelDescription;
import software.amazon.awssdk.services.lookoutvision.model.ModelStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

// Starts an Amazon Lookout for Vision model.
public class StartModel {

    public static final Logger logger = Logger.getLogger(StartModel.class.getName());

    public static void main(String[] args) {

        String projectName = null;
        String modelVersion = null;
        String minInferenceUnits = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <model_version>\n\n"
                + "Where:\n"
                + "   project_arn - the project in which you want to start a model.\n\n"
                + "   model_version - the version of the model that you want to start.\n\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];
        modelVersion = args[1];
        minInferenceUnits = args[2];

        try {

            System.out.println(String.format("Starting model version %s for project %s",
                    modelVersion, projectName));

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Start the model.
            ModelDescription modelDescription = Hosting.startModel(lfvClient,
                    projectName, modelVersion, Integer.parseInt(minInferenceUnits));

            if (modelDescription.status() == ModelStatus.HOSTED) {
                System.out.println(String.format("model version %s for project %s is running.",
                        modelVersion, projectName));

            } else {
                System.out.println(String.format("model version %s for  project %s status: %s",
                        modelVersion, projectName, modelDescription.statusMessage()));
            }

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not start model: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not start model: %s", lfvError.getMessage()));
            System.exit(1);
        } catch (InterruptedException intError) {
            logger.log(Level.SEVERE, "Interrupt exception occurred: {0}", intError.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
// snippet-end:[lookoutvision.java2.start_model.complete]