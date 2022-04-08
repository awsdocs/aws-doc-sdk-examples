/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.create_model.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelDescription;

import java.util.logging.Level;
import java.util.logging.Logger;

// Creates an Amazon Lookout for Vision model.
public class CreateModel {

    public static final Logger logger = Logger.getLogger(CreateModel.class.getName());

    public static void main(String[] args) {

        String projectName = null;
        String description = null;
        String bucket = null;
        String outputFolder = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <model_version> <bucket> <output_folder>\n\n"
                + "Where:\n"
                + "   project_arn - the project in which you want to create a model.\n\n"
                + "   description - a description for the model.\n\n"
                + "   bucket - the S3 bucket in which the service should store the training results.\n\n"
                + "   output_folder - the folder, in the S3 bucket, in which the service should store the training results.\n\n";

        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];
        description = args[1];
        bucket = args[2];
        outputFolder = args[3];

        try {

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Create the model and start training.
            ModelDescription modelDescription = Models.createModel(lfvClient, projectName, description,
                    bucket, outputFolder);

            // Show the model details
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
            logger.log(Level.SEVERE, "Could not create model: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not create model: %s", lfvError.getMessage()));
            System.exit(1);


        } catch (InterruptedException intError) {
            logger.log(Level.SEVERE, "Interrupt exception occurred: {0}", intError.getMessage());
            Thread.currentThread().interrupt();
        }                

       
    }

}
// snippet-end:[lookoutvision.java2.create_model.complete]