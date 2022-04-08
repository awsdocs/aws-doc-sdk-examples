/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.describe_model_packaging_job.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelPackagingDescription;

import java.util.logging.Level;
import java.util.logging.Logger;

// Describes an Amazon Lookout for Vision model packaging job.
public class DescribeModelPackagingJob {

    public static final Logger logger = Logger.getLogger(DescribeModelPackagingJob.class.getName());

    public static void main(String[] args) {

        String projectName = null;
        String jobName = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <job_name>\n\n"
                + "Where:\n"
                + "   project_arn - the project that contains the model packaging job that you want to describe.\n\n"
                + "   job_name - the name of the model packaging job that you want to describe.e\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];
        jobName = args[1];

        try {

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Get the model packaging job description.
            ModelPackagingDescription modelPackagingJob = EdgePackages.describeModelPackagingJob(lfvClient, projectName,
                    jobName);

            // Show the model packaging job details.
            System.out.println(String.format("Job: %s", modelPackagingJob.jobName()));
            System.out.println(String.format("Project: %s", modelPackagingJob.projectName()));
            System.out.println(String.format("Model version: %s", modelPackagingJob.modelVersion()));
            System.out.println(String.format("Status: %s", modelPackagingJob.statusAsString()));
            System.out.println(String.format("Message: %s", modelPackagingJob.statusMessage()));
            System.out.println();

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not describe model packaging job: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not describe model packaging job: %s", lfvError.getMessage()));
            System.exit(1);
        }
    }

}
// snippet-end:[lookoutvision.java2.describe_model_packaging_job.complete]
