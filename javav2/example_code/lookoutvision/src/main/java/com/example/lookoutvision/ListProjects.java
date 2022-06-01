/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.list_projects.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ProjectMetadata;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// List the Amazon Lookout for Vision projects in the current AWS account and AWS Region.
public class ListProjects {

    public static final Logger logger = Logger.getLogger(ListProjects.class.getName());

    public static void main(String[] args) throws Exception {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListProjects \n";

        try {

            if (args.length != 0) {
                System.out.println(USAGE);
                System.exit(1);
            }

            // Get the Lookout for Vision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            List<ProjectMetadata> projects = Projects.listProjects(lfvClient);

            System.out.printf("Projects%n--------%n");

            for (ProjectMetadata project : projects) {
                System.out.printf("Name: %s%n", project.projectName());
                System.out.printf("ARN: %s%n", project.projectArn());
                System.out.printf("Date: %s%n%n", project.creationTimestamp().toString());
            }

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not list projects: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not list projects: %s", lfvError.getMessage()));
            System.exit(1);
        }

    }

}
// snippet-end:[lookoutvision.java2.list_projects.complete]
