/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.delete_project.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.util.logging.Level;
import java.util.logging.Logger;

// Deletes an Amazon Lookout for Vision project.
public class DeleteProject {

    public static final Logger logger = Logger.getLogger(DeleteProject.class.getName());

    public static void main(String args[]) throws Exception {

        String projectName = null;

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteProject <project_name>  \n\n" +
                "Where:\n" +
                "    project_name - The name of the project that you want to delete.\n\n";

        try {

            if (args.length != 1) {
                System.out.println(USAGE);
                System.exit(1);
            }

            projectName = args[0];

            // Get the Lookout for Vision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            System.out.printf("Deleting project %s%n", projectName);
            Projects.deleteProject(lfvClient, projectName);
            System.out.printf("Project deleted: %s%n", projectName);

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not delete project: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not delete project: %s", lfvError.getMessage()));
            System.exit(1);
        }

    }

}
// snippet-end:[lookoutvision.java2.delete_project.complete]
