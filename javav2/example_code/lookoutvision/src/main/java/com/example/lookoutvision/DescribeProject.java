/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.describe_project.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ProjectDescription;

import java.util.logging.Level;
import java.util.logging.Logger;

// Describes an Amazon Lookout for Vision project.
public class DescribeProject {

    public static final Logger logger = Logger.getLogger(DescribeProject.class.getName());

    public static void main(String[] args) throws Exception {

        String projectName = null;

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DescribeProject <project_name>  \n\n" +
                "Where:\n" +
                "    project_name - the name of the project that you want to describe.\n\n";

        try {

            if (args.length != 1) {
                System.out.println(USAGE);
                System.exit(1);
            }

            projectName = args[0];

            // Get the Lookout for Vision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            System.out.printf("Describing project %s%n", projectName);
            ProjectDescription projectDescription = Projects.describeProject(lfvClient, projectName);
            System.out.println("Name: " + projectDescription.projectName());
            System.out.println("ARN: " + projectDescription.projectArn());
            System.out.println("Has datasets: " + projectDescription.hasDatasets());
            System.out.println("Created: " + projectDescription.creationTimestamp());

            System.out.printf("Project described: %s%n", projectName);

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not describe project: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not describe project: %s", lfvError.getMessage()));
            System.exit(1);
        }

    }

}
// snippet-end:[lookoutvision.java2.describe_project.complete]
