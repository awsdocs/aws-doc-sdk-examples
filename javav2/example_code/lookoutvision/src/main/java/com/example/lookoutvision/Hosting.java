/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.hosting.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;

import software.amazon.awssdk.services.lookoutvision.model.StartModelRequest;
import software.amazon.awssdk.services.lookoutvision.model.StopModelRequest;
import software.amazon.awssdk.services.lookoutvision.model.DescribeModelRequest;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelDescription;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

// Operations on Amazon Lookout for Vision models.
public class Hosting {

        public static final Logger logger = Logger.getLogger(Hosting.class.getName());

        /**
         * Starts hosting an Amazon Lookout for Vision model. Returns when the model has
         * started or if hosting fails. You are charged for the amount of time that
         * a model is hosted. To stop hosting a model, use the StopModel operation.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project that contains the model that you
         *                    want to host.
         * @modelVersion The version of the model that you want to host.
         * @minInferenceUnits The number of inference units to use for hosting.
         * @return ModelDescription The description of the model, which includes the
         *         model hosting status.
         */
        public static ModelDescription startModel(LookoutVisionClient lfvClient, String projectName,
                        String modelVersion, int minInferenceUnits)
                        throws LookoutVisionException, InterruptedException {

                logger.log(Level.INFO, "Starting Model version {0} for project {1}.",
                                new Object[] { modelVersion, projectName });

                StartModelRequest startModelRequest = StartModelRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .minInferenceUnits(minInferenceUnits)
                                .build();

                // Start hosting the model.
                lfvClient.startModel(startModelRequest);

                DescribeModelRequest describeModelRequest = DescribeModelRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .build();

                ModelDescription modelDescription = null;

                boolean finished = false;
                // Wait until model is hosted or failure occurs.
                do {

                        modelDescription = lfvClient.describeModel(describeModelRequest).modelDescription();

                        switch (modelDescription.status()) {

                                case HOSTED:
                                        logger.log(Level.INFO, "Model version {0} for project {1} is running.",
                                                        new Object[] { modelVersion, projectName });
                                        finished = true;
                                        break;

                                case STARTING_HOSTING:
                                        logger.log(Level.INFO, "Model version {0} for project {1} is starting.",
                                                        new Object[] { modelVersion, projectName });

                                        TimeUnit.SECONDS.sleep(60);

                                        break;
                                case HOSTING_FAILED:
                                        logger.log(Level.SEVERE,
                                                        "Hosting failed for model version {0} for project {1}.",
                                                        new Object[] { modelVersion, projectName });
                                        finished = true;
                                        break;

                                default:
                                        logger.log(Level.SEVERE,
                                                        "Unexpected error when hosting model version {0} for project {1}: {2}.",
                                                        new Object[] { projectName, modelVersion,
                                                                        modelDescription.status() });
                                        finished = true;
                                        break;

                        }

                } while (!finished);

                logger.log(Level.INFO, "Finished starting model version {0} for project {1} status: {2}",
                                new Object[] { modelVersion, projectName, modelDescription.statusMessage() });

                return modelDescription;

        }

        /**
         * Stops the hosting an Amazon Lookout for Vision model. Returns when model has
         * stopped or if hosting fails.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project that contains the model that you
         *                    want to stop hosting.
         * @modelVersion The version of the model that you want to stop hosting.
         * @return ModelDescription The description of the model, which includes the
         *         model hosting status.
         */

        public static ModelDescription stopModel(LookoutVisionClient lfvClient, String projectName,
                        String modelVersion) throws LookoutVisionException, InterruptedException {

                logger.log(Level.INFO, "Stopping Model version {0} for project {1}.",
                                new Object[] { modelVersion, projectName });

                StopModelRequest stopModelRequest = StopModelRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .build();

                // Stop hosting the model.

                lfvClient.stopModel(stopModelRequest);

                DescribeModelRequest describeModelRequest = DescribeModelRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .build();

                ModelDescription modelDescription = null;

                boolean finished = false;
                // Wait until model is stopped or failure occurs.
                do {

                        modelDescription = lfvClient.describeModel(describeModelRequest).modelDescription();

                        switch (modelDescription.status()) {

                                case TRAINED:
                                        logger.log(Level.INFO, "Model version {0} for project {1} has stopped.",
                                                        new Object[] { modelVersion, projectName });
                                        finished = true;
                                        break;

                                case STOPPING_HOSTING:
                                        logger.log(Level.INFO, "Model version {0} for project {1} is stopping.",
                                                        new Object[] { modelVersion, projectName });

                                        TimeUnit.SECONDS.sleep(60);

                                        break;

                                default:
                                        logger.log(Level.SEVERE,
                                                        "Unexpected error when stopping model version {0} for project {1}: {2}.",
                                                        new Object[] { projectName, modelVersion,
                                                                        modelDescription.status() });
                                        finished = true;
                                        break;

                        }

                } while (!finished);

                logger.log(Level.INFO, "Finished stopping model version {0} for project {1} status: {2}",
                                new Object[] { modelVersion, projectName, modelDescription.statusMessage() });

                return modelDescription;

        }
}

// snippet-end:[lookoutvision.java2.hosting.complete]