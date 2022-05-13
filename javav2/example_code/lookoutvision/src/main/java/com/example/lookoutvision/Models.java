/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.models.complete]

package com.example.lookoutvision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.CreateModelRequest;
import software.amazon.awssdk.services.lookoutvision.model.CreateModelResponse;
import software.amazon.awssdk.services.lookoutvision.model.DeleteModelRequest;
import software.amazon.awssdk.services.lookoutvision.model.DescribeModelRequest;
import software.amazon.awssdk.services.lookoutvision.model.DescribeModelResponse;
import software.amazon.awssdk.services.lookoutvision.model.ListModelsRequest;
import software.amazon.awssdk.services.lookoutvision.model.ListModelsResponse;
import software.amazon.awssdk.services.lookoutvision.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.lookoutvision.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelDescription;
import software.amazon.awssdk.services.lookoutvision.model.ModelMetadata;
import software.amazon.awssdk.services.lookoutvision.model.OutputConfig;
import software.amazon.awssdk.services.lookoutvision.model.S3Location;
import software.amazon.awssdk.services.lookoutvision.model.Tag;
import software.amazon.awssdk.services.lookoutvision.model.TagResourceRequest;
import software.amazon.awssdk.services.lookoutvision.model.UntagResourceRequest;

// Operations on Lookout for Vision models.
public class Models {

        public static final Logger logger = Logger.getLogger(Models.class.getName());

        /**
         * Creates an Amazon Lookout for Vision model. The function returns after model
         * training completes. Model training can take multiple hours to complete.
         * You are charged for the amount of time it takes to successfully train a model.
         * Returns after Lookout for Vision creates the dataset.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project in which you want to create a
         *                    model.
         * @param description A description for the model.
         * @param bucket      The S3 bucket in which Lookout for Vision stores the
         *                    training results.
         * @param folder      The location of the training results within the S3
         *                    bucket.
         * @return ModelDescription The description of the created model.
         */
        public static ModelDescription createModel(LookoutVisionClient lfvClient, String projectName,
                        String description, String bucket, String folder)
                        throws LookoutVisionException, InterruptedException {

                logger.log(Level.INFO, "Creating model for project: {0}.", new Object[] { projectName });

                // Setup input parameters.
                S3Location s3Location = S3Location.builder()
                                .bucket(bucket)
                                .prefix(folder)
                                .build();

                OutputConfig config = OutputConfig.builder()
                                .s3Location(s3Location)
                                .build();

                CreateModelRequest createModelRequest = CreateModelRequest.builder()
                                .projectName(projectName)
                                .description(description)
                                .outputConfig(config)
                                .build();

                // Create and train the model.
                CreateModelResponse response = lfvClient.createModel(createModelRequest);

                String modelVersion = response.modelMetadata().modelVersion();
                boolean finished = false;
                DescribeModelResponse descriptionResponse = null;

                // Wait until training finishes or fails.

                do {
                        DescribeModelRequest describeModelRequest = DescribeModelRequest.builder()
                                        .projectName(projectName)
                                        .modelVersion(modelVersion)
                                        .build();

                        descriptionResponse = lfvClient.describeModel(describeModelRequest);

                        switch (descriptionResponse.modelDescription().status()) {
                                case TRAINED:
                                        logger.log(Level.INFO, "Model training completed for project {0} version {1}.",
                                                        new Object[] { projectName, modelVersion });
                                        finished = true;
                                        break;

                                case TRAINING:
                                        logger.log(Level.INFO,
                                                        "Model training in progress for project {0} version {1}.",
                                                        new Object[] { projectName, modelVersion });
                                        TimeUnit.SECONDS.sleep(60);

                                        break;

                                case TRAINING_FAILED:
                                        logger.log(Level.SEVERE,
                                                        "Model training failed for for project {0} version {1}.",
                                                        new Object[] { projectName, modelVersion });
                                        finished = true;
                                        break;

                                default:
                                        logger.log(Level.SEVERE,
                                                        "Unexpected error when training model project {0} version {1}: {2}.",
                                                        new Object[] { projectName, modelVersion,
                                                                        descriptionResponse.modelDescription()
                                                                                        .status() });
                                        finished = true;
                                        break;

                        }
                } while (!finished);

                return descriptionResponse.modelDescription();

        }

        /**
         * Describes an Amazon Lookout for Vision model.
         * 
         * @param lfvClient    An Amazon Lookout for Vision client.
         * @param projectName  The name of the project that contains the model that you
         *                     want to describe.
         * 
         * @param modelVersion The version of the model that you want to describe.
         * 
         * @return ModelDescription The description of the model.
         */
        public static ModelDescription describeModel(LookoutVisionClient lfvClient, String projectName,
                        String modelVersion) throws LookoutVisionException {

                logger.log(Level.INFO, "Describing model: {0} version {1}", new Object[] { projectName, modelVersion });

                DescribeModelRequest describeModelRequest = DescribeModelRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .build();

                DescribeModelResponse descriptionResponse = lfvClient.describeModel(describeModelRequest);

                ModelDescription modelDescription = descriptionResponse.modelDescription();

                logger.log(Level.INFO, "Model ARN: {0}\nVersion: {1}\nStatus: {2}\nMessage: {3}", new Object[] {
                                modelDescription.modelArn(),
                                modelDescription.modelVersion(),
                                modelDescription.statusMessage(),
                                modelDescription.statusAsString() });

                return modelDescription;

        }

        /**
         * Lists the models in an Amazon Lookout for Vision project.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project that contains the models that
         *                    you want to list.
         * @return List <Metadata> A list of models in the project.
         */
        public static List<ModelMetadata> listModels(LookoutVisionClient lfvClient, String projectName)
                        throws LookoutVisionException {

                ListModelsRequest listModelsRequest = ListModelsRequest.builder()
                                .projectName(projectName)
                                .build();

                // Get a list of models in the supplied project.
                ListModelsResponse response = lfvClient.listModels(listModelsRequest);

                for (ModelMetadata model : response.models()) {
                        logger.log(Level.INFO, "Model ARN: {0}\nVersion: {1}\nStatus: {2}\nMessage: {3}", new Object[] {
                                        model.modelArn(),
                                        model.modelVersion(),
                                        model.statusMessage(),
                                        model.statusAsString() });
                }

                return response.models();

        }


        /**
         * Lists the tags attached to an Amazon Lookout for Vision model.
         * 
         * @param lfvClient    An Amazon Lookout for Vision client.
         * @param projectName  The name of the project that contains the model for which
         *                     you want to list attached tags.
         * @param modelVersion The version of the model for which you want to list the
         *                     attached tags.
         * @return List <Tag> A list of tags attached to the model.
         */
        public static List<Tag> listTagsForModel(LookoutVisionClient lfvClient,
                        String projectName, String modelVersion) {

                logger.log(Level.INFO, "Getting tags for model version {0} of project {1}.",
                                new Object[] { modelVersion, projectName });

                DescribeModelRequest describeModelRequest = DescribeModelRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .build();

                ModelDescription modelDescription = lfvClient.describeModel(describeModelRequest).modelDescription();

                ListTagsForResourceRequest listTagsForResourceRequest = ListTagsForResourceRequest.builder()
                                .resourceArn(modelDescription.modelArn())
                                .build();

                // Get a list of tags attached to the model.
                ListTagsForResourceResponse response = lfvClient.listTagsForResource(listTagsForResourceRequest);

                if (response.hasTags()) {
                        for (Tag tag : response.tags()) {
                                logger.log(Level.INFO, "Key: {0}\nValue:  {1}\n\n", new Object[] {
                                                tag.key(),
                                                tag.value() });
                        }
                } else {
                        logger.log(Level.INFO, "No tags found.");
                }

                return response.tags();

        }
        /**
        * Unattaches a tag from an Amazon Lookout for Vision model.
        * 
        * @param lfvClient    An Amazon Lookout for Vision client.
        * @param projectName  The name of the project that contains the model for which
        *                     you want to unattach a tag.
        * @param modelVersion The version of the model for which you want to unattach a tag.
        * @return void
        */

        public static void untagModel(LookoutVisionClient lfvClient, String projectName, String modelVersion,
                        String key) throws LookoutVisionException {

                logger.log(Level.INFO, "Untagging key {0} from model version {1} of project {2}.",
                                new Object[] { key, modelVersion, projectName });

                // Set up tag key.
                Collection<String> tagKeys = new ArrayList<>();
                tagKeys.add(key);

                // Get the model Amazon Resource Name (ARN).
                DescribeModelRequest describeModelRequest = DescribeModelRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .build();

                ModelDescription modelDescription = lfvClient.describeModel(describeModelRequest).modelDescription();

                // Untag the resource.
                UntagResourceRequest unTagResourceRequest = UntagResourceRequest.builder()
                                .resourceArn(modelDescription.modelArn())
                                .tagKeys(tagKeys)
                                .build();

                lfvClient.untagResource(unTagResourceRequest);

                logger.log(Level.INFO, "key {0} untagged from model version {1} of project {2}.",
                                new Object[] { key, modelVersion, projectName });

        }

        /**
        * Attaches a tag to an Amazon Lookout for Vision model.
        * 
        * @param lfvClient    An Amazon Lookout for Vision client.
        * @param projectName  The name of the project that contains the model for which
        *                     you want to attach a tag.
        * @param modelVersion The version of the model for which you want to attach a tag.
        * @return void
        */

        public static void tagModel(LookoutVisionClient lfvClient, String projectName, String modelVersion,
                        String key, String value) throws LookoutVisionException {

                logger.log(Level.INFO, "Tagging model version {0} of project {1}. Key: {2} Value {3}.",
                                new Object[] { modelVersion, projectName, key, value });

                // Set up tag.
                Collection<Tag> tags = new ArrayList<>();

                Tag tag = Tag.builder()
                                .key(key)
                                .value(value)
                                .build();

                tags.add(tag);

                // Get the model ARN.
                DescribeModelRequest describeModelRequest = DescribeModelRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .build();

                ModelDescription modelDescription = lfvClient.describeModel(describeModelRequest).modelDescription();

                // Tag the resource.
                TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                                .resourceArn(modelDescription.modelArn())
                                .tags(tags)
                                .build();

                lfvClient.tagResource(tagResourceRequest);

                logger.log(Level.INFO, "Tagged model version {0} of project {1} with Key: {2} and value: {3}.",
                                new Object[] { modelVersion, projectName, key, value });

        }

        /**
        * Deletes an Amazon Lookout for Vision model.
        * 
        * @param lfvClient    An Amazon Lookout for Vision client. Returns after the model is deleted.
        * @param projectName  The name of the project that contains the model that you want to delete.
        * @param modelVersion The version of the model that you want to delete.
        * @return void
        */
        public static void deleteModel(LookoutVisionClient lfvClient,
                        String projectName,
                        String modelVersion) throws LookoutVisionException, InterruptedException {

                DeleteModelRequest deleteModelRequest = DeleteModelRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .build();

                lfvClient.deleteModel(deleteModelRequest);

                boolean deleted = false;

                do {

                        ListModelsRequest listModelsRequest = ListModelsRequest.builder()
                                        .projectName(projectName)
                                        .build();

                        // Get a list of models in the supplied project.
                        ListModelsResponse response = lfvClient.listModels(listModelsRequest);

                        ModelMetadata modelMetadata = response.models().stream()
                                        .filter(model -> model.modelVersion().equals(modelVersion)).findFirst()
                                        .orElse(null);

                        if (modelMetadata == null) {
                                deleted = true;
                                logger.log(Level.INFO, "Deleted: Model version {0} of project {1}.",
                                                new Object[] { modelVersion, projectName });

                        } else {
                                logger.log(Level.INFO, "Not yet deleted: Model version {0} of project {1}.",
                                                new Object[] { modelVersion, projectName });
                                TimeUnit.SECONDS.sleep(60);
                        }

                } while (!deleted);

        }

}

// snippet-end:[lookoutvision.java2.models.complete]
