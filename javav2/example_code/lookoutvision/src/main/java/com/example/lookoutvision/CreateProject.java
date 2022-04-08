/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.create_project.complete]

package com.example.lookoutvision;

import java.util.logging.Level;
import java.util.logging.Logger;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ProjectMetadata;

// Creates an Amazon Lookout for Vision project.
public class CreateProject {

    public static final Logger logger = Logger.getLogger(CreateProject.class.getName());

    public static void main(String args[]) throws Exception {

        String projectName = null;

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateProject <project_name>  \n\n" +
                "Where:\n" +
                "    project_name - the name of the project that you want to create.\n\n";

        try {

            if (args.length != 1) {
                System.out.println(USAGE);
                System.exit(1);
            }

            projectName = args[0];

            // Get the Lookout for Vision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            System.out.printf("Creating project %s%n", projectName);

            ProjectMetadata project = Projects.createProject(lfvClient, projectName);

            System.out.println("Project created");
            System.out.printf("Name: %s%n", project.projectName());
            System.out.printf("ARN: %s%n", project.projectArn());
            System.out.printf("Date: %s%n", project.creationTimestamp().toString());

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not create project: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not create project: %s", lfvError.getMessage()));
            System.exit(1);
        }

    }

}
// snippet-end:[lookoutvision.java2.create_project.complete]
