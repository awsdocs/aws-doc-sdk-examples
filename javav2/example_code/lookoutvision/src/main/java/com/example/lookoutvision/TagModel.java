/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.tag_model.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.util.logging.Level;
import java.util.logging.Logger;

// Tags an Amazon Lookout for Vision model.
public class TagModel {

    public static final Logger logger = Logger.getLogger(TagModel.class.getName());

    public static void main(String[] args) {

        String projectName = null;
        String modelVersion = null;
        String key = null;
        String value = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <model_version>\n\n"
                + "Where:\n"
                + "   project_arn - the project that contains the model that you to add a tag to.\n\n"
                + "   model_version - the version of the model that you want to add a tag to.\n\n"
                + "   key - the key for the tag.\n\n"
                + "   value - the value of the tag.\n\n";
        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];
        modelVersion = args[1];
        key = args[2];
        value = args[3];

        try {

            System.out.println(String.format("Tagging model version %s of project %s with key %s and value %s.",
                    modelVersion, projectName, key, value));

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Tag the model.
            Models.tagModel(lfvClient, projectName, modelVersion, key, value);

            System.out.println(String.format("Tagged model version %s of project %s with key %s and value %s.",
                    modelVersion, projectName, key, value));

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not tag model: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not tag model: %s", lfvError.getMessage()));
            System.exit(1);
        }
    }

}

// snippet-end:[lookoutvision.java2.tag_model.complete]