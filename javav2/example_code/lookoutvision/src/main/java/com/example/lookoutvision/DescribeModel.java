/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.describe_model.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelDescription;

import java.util.logging.Level;
import java.util.logging.Logger;

// Describes an Amazon Lookout for Vision model.
public class DescribeModel {

    public static final Logger logger = Logger.getLogger(DescribeModel.class.getName());

    public static void main(String[] args) {

        String projectName = null;
        String modelVersion = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <model_version>\n\n"
                + "Where:\n"
                + "   project_arn - The project that contains the model that you want to describe.\n\n"
                + "   model_version - The version of the model that you want to describe\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];
        modelVersion = args[1];

        try {

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Get the model description.
            ModelDescription modelDescription = Models.describeModel(lfvClient, projectName, modelVersion);

            // Show the model details.
            System.out.println(String.format("Model Project: %s", projectName));
            System.out.println(String.format("Version: %s", modelDescription.modelVersion()));
            System.out.println(String.format("ARN: %s", modelDescription.modelArn()));
            System.out.println(String.format("Status: %s", modelDescription.statusAsString()));
            System.out.println(String.format("Status message: %s", modelDescription.statusMessage()));
            if (modelDescription.performance() != null) {
                System.out.println("Testing results");
                System.out.println(String.format("F1: %s", modelDescription.performance().f1Score().toString()));
                System.out
                        .println(String.format("Precision: %s", modelDescription.performance().precision().toString()));
                System.out.println(String.format("recall: %s", modelDescription.performance().recall().toString()));
                System.out.println(String.format("More results: s3://%s/%s",
                        modelDescription.evaluationResult().bucket(),
                        modelDescription.evaluationResult().key()));
            }

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not describe model: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not describe model: %s", lfvError.getMessage()));
            System.exit(1);
        }
    }

}
// snippet-end:[lookoutvision.java2.describe_model.complete]