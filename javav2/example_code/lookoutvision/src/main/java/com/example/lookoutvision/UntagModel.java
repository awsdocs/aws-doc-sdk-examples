/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.untag_model.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.util.logging.Level;
import java.util.logging.Logger;

// Untag an Amazon Lookout for Vision model.
public class UntagModel {

    public static final Logger logger = Logger.getLogger(UntagModel.class.getName());

    public static void main(String[] args) {

        String projectName = null;
        String modelVersion = null;
        String key = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <model_version>\n\n"
                + "Where:\n"
                + "   project_arn - The project that contains the model that you to untage.\n\n"
                + "   model_version - The version of the model that you want to untag.\n\n"
                + "   key - The key for the tag that you want to remove.\n\n";
        ;
        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];
        modelVersion = args[1];
        key = args[2];

        try {

            System.out.println(String.format("Untagging key %s from model version %s of project %s.",
                    key, modelVersion, projectName));

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Untag the model.
            Models.untagModel(lfvClient, projectName, modelVersion, key);

            System.out.println(String.format("Key %s untagged from model version %s of project %s.",
                    key, modelVersion, projectName));

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not untag model: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not untag model: %s", lfvError.getMessage()));
            System.exit(1);
        }
    }

}

// snippet-end:[lookoutvision.java2.untag_model.complete]