/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.edge_packages.complete]

package com.example.lookoutvision;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.DescribeModelPackagingJobRequest;
import software.amazon.awssdk.services.lookoutvision.model.DescribeModelPackagingJobResponse;
import software.amazon.awssdk.services.lookoutvision.model.GreengrassConfiguration;
import software.amazon.awssdk.services.lookoutvision.model.ListModelPackagingJobsRequest;
import software.amazon.awssdk.services.lookoutvision.model.ListModelPackagingJobsResponse;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelPackagingConfiguration;
import software.amazon.awssdk.services.lookoutvision.model.ModelPackagingDescription;
import software.amazon.awssdk.services.lookoutvision.model.ModelPackagingJobMetadata;
import software.amazon.awssdk.services.lookoutvision.model.S3Location;
import software.amazon.awssdk.services.lookoutvision.model.StartModelPackagingJobRequest;
import software.amazon.awssdk.services.lookoutvision.model.Tag;
import software.amazon.awssdk.services.lookoutvision.model.TargetPlatform;

// Operations on Amazon Lookout for Vision edge packages.
public class EdgePackages {

        public static final Logger logger = Logger.getLogger(EdgePackages.class.getName());

        /**
         * Lists the model packaging jobs in an Amazon Lookout for Vision project.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project for which you want to list the
         *                    model packaging jobs.
         * @return List<ModelPackagingJobMetadata> Metadata for each model packaging
         *         job.
         */
        public static List<ModelPackagingJobMetadata> listModelPackagingJobs(LookoutVisionClient lfvClient,
                        String projectName) {

                logger.log(Level.INFO, "Listing model packaging jobs in project {0}.",
                                projectName);

                ListModelPackagingJobsRequest listModelPackagingJobsRequest = ListModelPackagingJobsRequest.builder()
                                .projectName(projectName)
                                .build();

                // Get a list of model packaging jobs in the supplied project.
                ListModelPackagingJobsResponse response = lfvClient
                                .listModelPackagingJobs(listModelPackagingJobsRequest);

                for (ModelPackagingJobMetadata modelPackagingJob : response.modelPackagingJobs()) {
                        logger.log(Level.INFO,
                                        "Job name: {0}\nProject: {1}\nModel version: {2}\nStatus: {3}\nMessage :{4}\n",
                                        new Object[] {
                                                        modelPackagingJob.jobName(),
                                                        modelPackagingJob.projectName(),
                                                        modelPackagingJob.modelVersion(),
                                                        modelPackagingJob.statusAsString(),
                                                        modelPackagingJob.statusMessage() });
                }

                return response.modelPackagingJobs();

        }

        /**
         * Describes a Amazon Lookout for Vision model packaging job.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project for which contains the model
         *                    packaging job that you want to describe.
         * @param jobName     The name of the model packaging job that you want to
         *                    describe.
         * @return ModelPackagingDescription A description for the requested model
         *         packaging job.
         */

        public static ModelPackagingDescription describeModelPackagingJob(LookoutVisionClient lfvClient,
                        String projectName,
                        String jobName) {

                logger.log(Level.INFO, "Describing model packaging job: {0} version {1}",
                                new Object[] { projectName, jobName });

                DescribeModelPackagingJobRequest describeModelRequest = DescribeModelPackagingJobRequest.builder()
                                .projectName(projectName)
                                .jobName(jobName)
                                .build();

                DescribeModelPackagingJobResponse response = lfvClient.describeModelPackagingJob(describeModelRequest);

                ModelPackagingDescription modelPackagingJobDescription = response.modelPackagingDescription();

                logger.log(Level.INFO, "Job name: {0}\nProject: {1}\nModel version: {2}\nStatus: {3}\nMessage :{4}\n",
                                new Object[] {
                                                modelPackagingJobDescription.jobName(),
                                                modelPackagingJobDescription.projectName(),
                                                modelPackagingJobDescription.modelVersion(),
                                                modelPackagingJobDescription.statusAsString(),
                                                modelPackagingJobDescription.statusMessage() });

                return modelPackagingJobDescription;

        }

        /**
         * Create a StartModelPackagingRequest object from a JSON file that
         * matches the request syntax for StartModelPackagingJob.
         * 
         * @param projectName The name of the project for which contains the model
         *                    packaging job that you want to describe.
         * @param fileName    The name and path of JSON file that matches the request
         *                    syntax for StartModelPackagingJob.
         * 
         * @return StartModelPackagingJobRequest A StartModelPackagingJobRequest object
         *         that you use with StartModelPackagingJob.
         * 
         */
        public static StartModelPackagingJobRequest buildPackagingJobRequest(String projectName, String fileName)
                        throws IOException {

                String description;
                String modelVersion;
                String jobName;

                ModelPackagingConfiguration modelPackagingConfiguration = null;
                StartModelPackagingJobRequest startModelPackagingJobRequest = null;
                GreengrassConfiguration greenGrassConfiguration = null;

                // Create a map from the JSON file.
                ObjectMapper mapper = new ObjectMapper();

                Map<?, ?> map = mapper.readValue(Paths.get(fileName).toFile(), Map.class);

                jobName = (String) map.get("JobName");
                description = (String) map.get("Description");
                modelVersion = (String) map.get("ModelVersion");

                // Get configuration field.
                LinkedHashMap<?, ?> configuration = (LinkedHashMap<?, ?>) map.get("Configuration");

                // Get greengrass configuration fields.
                LinkedHashMap<?, ?> greenGrass = (LinkedHashMap<?, ?>) configuration.get("Greengrass");
                LinkedHashMap<?, ?> s3Object = (LinkedHashMap<?, ?>) greenGrass.get("S3OutputLocation");

                S3Location s3OutputLocation = S3Location.builder()
                                .bucket((String) s3Object.get("Bucket"))
                                .prefix((String) s3Object.get("Prefix"))
                                .build();

                Collection<Tag> tagAssignments = null;
                Collection<?> tags = (Collection<?>) greenGrass.get("Tags");

                if (tags != null) {
                        // Build tags list, if tags present.

                        tagAssignments = new ArrayList<>();

                        for (Object tag : tags) {
                                LinkedHashMap<?, ?> tagHM = (LinkedHashMap<?, ?>) tag;

                                Tag tagAssignment = Tag.builder()
                                                .key((String) tagHM.get("Key"))
                                                .value((String) tagHM.get("Value"))
                                                .build();
                                tagAssignments.add(tagAssignment);
                        }

                }

                // Set up for target device or target hardware.
                if (greenGrass.containsKey("TargetDevice")) {

                        greenGrassConfiguration = GreengrassConfiguration.builder()
                                        .componentName((String) greenGrass.get("ComponentName"))
                                        .componentVersion((String) greenGrass.get("ComponentVersion"))
                                        .s3OutputLocation(s3OutputLocation)
                                        .targetDevice((String) greenGrass.get("TargetDevice"))
                                        .tags(tagAssignments)
                                        .build();
                }
                if (greenGrass.containsKey("TargetPlatform")) {

                        LinkedHashMap<?, ?> target = (LinkedHashMap<?, ?>) greenGrass.get("TargetPlatform");

                        TargetPlatform targetPlatform = TargetPlatform.builder()
                                        .accelerator((String) target.get("Accelerator"))
                                        .arch((String) target.get("Arch"))
                                        .os((String) target.get("Os"))
                                        .build();

                        greenGrassConfiguration = GreengrassConfiguration.builder()
                                        .componentName((String) greenGrass.get("ComponentName"))
                                        .componentVersion((String) greenGrass.get("ComponentVersion"))
                                        .s3OutputLocation(s3OutputLocation)
                                        .targetPlatform(targetPlatform)
                                        .compilerOptions((String) greenGrass.get("CompilerOptions"))
                                        .tags(tagAssignments)
                                        .build();
                }

                modelPackagingConfiguration = ModelPackagingConfiguration.builder()
                                .greengrass(greenGrassConfiguration)
                                .build();

                // Build the request.
                startModelPackagingJobRequest = StartModelPackagingJobRequest.builder()
                                .projectName(projectName)
                                .modelVersion(modelVersion)
                                .description(description)
                                .jobName(jobName)
                                .configuration(modelPackagingConfiguration)
                                .build();

                return startModelPackagingJobRequest;

        }

        /**
         * Starts an Amazon Lookout for Vision model packaging job. The operation
         * returns after the model is packaged.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project for which contains the model
         *                    packaging job that you want to describe.
         * @param fileName    The name and path of a JSON file that matches the request
         *                    syntax for StartModelPackagingJob.
         * @return ModelPackagingDescription A description for the model
         *         packaging job.
         */
        public static ModelPackagingDescription startModelPackagingJob(LookoutVisionClient lfvClient,
                        String projectName,
                        String fileName) throws IOException, LookoutVisionException, InterruptedException {

                logger.log(Level.INFO, "Starting model packaging job for project : {0}.", new Object[] { projectName });

                StartModelPackagingJobRequest startModelPackagingJobRequest = buildPackagingJobRequest(projectName,
                                fileName);

                // Start the model packaging job.
                lfvClient.startModelPackagingJob(startModelPackagingJobRequest);

                DescribeModelPackagingJobRequest describeModelPackagingJobRequest = DescribeModelPackagingJobRequest
                                .builder()
                                .projectName(startModelPackagingJobRequest.projectName())
                                .jobName(startModelPackagingJobRequest.jobName())
                                .build();

                ModelPackagingDescription modelPackagingJobDescription = null;
                boolean finished = false;

                // Wait until model packaging job finishes or fails.
                do {

                        DescribeModelPackagingJobResponse describeModelPackagingJobResponse = lfvClient
                                        .describeModelPackagingJob(describeModelPackagingJobRequest);

                        modelPackagingJobDescription = describeModelPackagingJobResponse.modelPackagingDescription();

                        switch (modelPackagingJobDescription.status()) {
                                case CREATED:

                                        logger.log(Level.INFO, "Model packaging job started for job {0}.",
                                                        startModelPackagingJobRequest.jobName());

                                        TimeUnit.SECONDS.sleep(60);

                                        break;

                                case SUCCEEDED:
                                        logger.log(Level.INFO, "Model packaging job succeeded for job {0}.",
                                                        startModelPackagingJobRequest.jobName());
                                        finished = true;
                                        break;

                                case RUNNING:
                                        logger.log(Level.INFO, "Model packaging for job {0} is running.",
                                                        startModelPackagingJobRequest.jobName());
                                        TimeUnit.SECONDS.sleep(60);

                                        break;

                                case FAILED:
                                        logger.log(Level.SEVERE, "Model packaging failed for job{0}.",
                                                        startModelPackagingJobRequest.jobName());
                                        finished = true;
                                        break;

                                default:
                                        logger.log(Level.SEVERE, "Unexpected error with packaging model job {0}.",
                                                        startModelPackagingJobRequest.jobName());
                                        finished = true;
                                        break;

                        }
                } while (!finished);

                return modelPackagingJobDescription;

        }
}
// snippet-end:[lookoutvision.java2.edge_packages.complete]