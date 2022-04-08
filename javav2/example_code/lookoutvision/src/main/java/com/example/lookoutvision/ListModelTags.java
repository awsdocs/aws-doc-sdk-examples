/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.list_tags_for_model.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.Tag;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Lists the tags attached to an Amazon Lookout for Vision model.
public class ListModelTags {

    public static final Logger logger = Logger.getLogger(ListModelTags.class.getName());

    public static void main(String[] args) {

        String projectName = null;
        String modelVersion = null;

        final String USAGE = "\n" + "Usage: " + "<project_name> <model_version>\n\n"
                + "Where:\n"
                + "   project_arn - the project that contains the model that you to list tags for.\n\n"
                + "   model_version - the version of the model that you want to list tags for.\n\n";
        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        projectName = args[0];
        modelVersion = args[1];

        try {

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            // Get a list of models in the supplied project
            List<Tag> tags = Models.listTagsForModel(lfvClient, projectName, modelVersion);

            System.out.println(String.format("Tags for model version %s of project %s.", modelVersion, projectName));

            if (!tags.isEmpty()) {

                for (Tag tag : tags) {
                    System.out.println(String.format("Key: %s%nValue: %s%n%n", tag.key(), tag.value()));
                }
            } else {
                logger.log(Level.INFO, "No tags found.");
            }

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not list model tags: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not list model tags: %s", lfvError.getMessage()));
            System.exit(1);
        }
    }

}

// snippet-end:[lookoutvision.java2.list_tags_for_model.complete]