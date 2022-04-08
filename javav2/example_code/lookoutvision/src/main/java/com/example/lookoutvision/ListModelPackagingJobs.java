/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.list_model_packaging_jobs.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelPackagingJobMetadata;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Lists the model packaging jobs in an Amazon Lookout for Vision  project.
public class ListModelPackagingJobs {

    public static final Logger logger = Logger.getLogger(ListModelPackagingJobs.class.getName());

    public static void main(String[] args) {

        String projectName = null;

        final String USAGE = "\n" + "Usage: " + "<project_name>\n\n"
                + "Where:\n"
                + "   project_arn - the project that contains the models packaging jobs that you want to list.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];

        try {

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Get a list of model packaging jobs in the supplied project
            List<ModelPackagingJobMetadata> modelPackagingJobs = EdgePackages.listModelPackagingJobs(lfvClient, projectName);

            System.out.println(String.format("Project: %s", projectName));

            if (!modelPackagingJobs.isEmpty()) {

                for (ModelPackagingJobMetadata modelPackagingJob : modelPackagingJobs) {
                    // Show the model packaging job details
                    System.out.println(String.format("Job: %s", modelPackagingJob.jobName()));
                    System.out.println(String.format("Project: %s", modelPackagingJob.projectName()));
                    System.out.println(String.format("Model version: %s", modelPackagingJob.modelVersion()));
                    System.out.println(String.format("Status: %s", modelPackagingJob.statusAsString()));
                    System.out.println(String.format("Message: %s", modelPackagingJob.statusMessage()));
                    System.out.println();
                }
            } else {
                System.out.println("No model packaging jobs found.");
            }

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not list model packaging jobs: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not list model packaging jobs: %s", lfvError.getMessage()));
            System.exit(1);
        }
    }

}
// snippet-end:[lookoutvision.java2.list_model_packaging_jobs.complete]