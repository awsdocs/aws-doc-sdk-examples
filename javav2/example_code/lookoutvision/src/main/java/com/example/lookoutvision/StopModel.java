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

// Stops a hosted Amazon Lookout for Vision model.
public class StopModel {

    public static final Logger logger = Logger.getLogger(StopModel.class.getName());

    public static void main(String[] args) {

        String projectName = null;
        String modelVersion = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <model_version>\n\n"
                + "Where:\n"
                + "   project_arn - the project in which you want to stop a model.\n\n"
                + "   model_version - the version of the model that you want to stop.\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];
        modelVersion = args[1];

        try {

            System.out.println(String.format("Stopping model version %s for Project %s",
                    modelVersion, projectName));

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Stop the model.
            ModelDescription modelDescription = Hosting.stopModel(lfvClient,
                    projectName, modelVersion);

            if (modelDescription.status() == ModelStatus.TRAINED) {
                System.out.println(String.format("model version %s for project %s has stopped.",
                        modelVersion, projectName));

            } else {
                System.out.println(String.format("model version %s for  project %s status: %s",
                        modelVersion, projectName, modelDescription.statusMessage()));
            }

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not stop model: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not stop model: %s", lfvError.getMessage()));
            System.exit(1);
        } catch (InterruptedException intError) {
            logger.log(Level.SEVERE, "Interrupt occurred: {0}", intError.getMessage());
            System.exit(1);
        }
    }

}
// snippet-end:[lookoutvision.java2.stop_model.complete]