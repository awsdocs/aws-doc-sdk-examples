/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.start_model_packaging_job.complete]

package com.example.lookoutvision;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelPackagingDescription;

// Start an Amazon Lookout for Vision model packaging job.
public class StartModelPackagingJob {

    public static final Logger logger = Logger.getLogger(StartModelPackagingJob.class.getName());

    public static void main(String[] args) {

        String projectName = null;
        String fileName = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <model_version> <job_name> "
                + "<component_name> <component_description> <component_version> <output_bucket> <output_folder>\n\n"
                + "Where:\n"
                + "   project_arn - The project in which you want to create a model.\n\n"
                + "   description - A description for the model.\n\n"
                + "   bucket - The S3 bucket in which the service should store the training results.\n\n"
                + "   output_folder - The folder, in the S3 bucket, in which the service should store the training results.\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];
        fileName = args[1];

        try {

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Start the model packaging job.

            ModelPackagingDescription modelPackagingJob = EdgePackages.startModelPackagingJob(lfvClient, projectName,
                    fileName);

            // Show the model packaging job details.
            System.out.println(String.format("Job: %s", modelPackagingJob.jobName()));
            System.out.println(String.format("Project: %s", modelPackagingJob.projectName()));
            System.out.println(String.format("Model version: %s", modelPackagingJob.modelVersion()));
            System.out.println(String.format("Status: %s", modelPackagingJob.statusAsString()));
            System.out.println(String.format("Message: %s", modelPackagingJob.statusMessage()));
            System.out.println();

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not start model packaging job: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Problem starting model packaging job: %s", lfvError.getMessage()));
            System.exit(1);
        }

        catch (IOException ioError) {
            logger.log(Level.SEVERE, "JSON file error: {0}", ioError.getMessage());
            System.out.println(String.format("Problem reading packaging job JSON file: %s", ioError.getMessage()));
            System.exit(1);
        } catch (InterruptedException intError) {
            logger.log(Level.SEVERE, "Interrupt exception occurred: {0}", intError.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
// snippet-end:[lookoutvision.java2.start_model_packaging_job.complete]