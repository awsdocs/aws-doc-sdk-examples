/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.datasets.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.CreateDatasetRequest;
import software.amazon.awssdk.services.lookoutvision.model.DatasetDescription;
import software.amazon.awssdk.services.lookoutvision.model.DatasetGroundTruthManifest;
import software.amazon.awssdk.services.lookoutvision.model.DatasetSource;
import software.amazon.awssdk.services.lookoutvision.model.DatasetStatus;
import software.amazon.awssdk.services.lookoutvision.model.DeleteDatasetRequest;
import software.amazon.awssdk.services.lookoutvision.paginators.ListDatasetEntriesIterable;
import software.amazon.awssdk.services.lookoutvision.model.DescribeDatasetRequest;
import software.amazon.awssdk.services.lookoutvision.model.DescribeDatasetResponse;
import software.amazon.awssdk.services.lookoutvision.model.InputS3Object;
import software.amazon.awssdk.services.lookoutvision.model.ListDatasetEntriesRequest;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.UpdateDatasetEntriesRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

// Operations on Lookout for Vision datasets.
public class Datasets {

        public static final Logger logger = Logger.getLogger(Datasets.class.getName());

        /**
         * Creates an Amazon Lookout for Vision dataset from a manifest file.
         * Returns after Lookout for Vision creates the dataset.
         * 
         * @param lfvClient    An Amazon Lookout for Vision client.
         * @param projectName  The name of the project in which you want to create a
         *                     dataset.
         * @param datasetType  The type of the dataset that you want to create (train or
         *                     test).
         * @param bucket       The S3 bucket that contains the manifest file.
         * @param manifestFile The name and location of the manifest file within the S3
         *                     bucket.
         * @return DatasetDescription The description of the created dataset.
         */
        public static DatasetDescription createDataset(LookoutVisionClient lfvClient,
                        String projectName,
                        String datasetType,
                        String bucket,
                        String manifestFile)
                        throws LookoutVisionException, InterruptedException {

                logger.log(Level.INFO, "Creating {0} dataset for project {1}",
                                new Object[] { projectName, datasetType });

                // Build the request. If no bucket supplied, setup for empty dataset creation.
                CreateDatasetRequest createDatasetRequest = null;

                if (bucket != null && manifestFile != null) {

                        InputS3Object s3Object = InputS3Object.builder()
                                        .bucket(bucket)
                                        .key(manifestFile)
                                        .build();

                        DatasetGroundTruthManifest groundTruthManifest = DatasetGroundTruthManifest.builder()
                                        .s3Object(s3Object)
                                        .build();

                        DatasetSource datasetSource = DatasetSource.builder()
                                        .groundTruthManifest(groundTruthManifest)
                                        .build();

                        createDatasetRequest = CreateDatasetRequest.builder()
                                        .projectName(projectName)
                                        .datasetType(datasetType)
                                        .datasetSource(datasetSource)
                                        .build();
                } else {
                        createDatasetRequest = CreateDatasetRequest.builder()
                                        .projectName(projectName)
                                        .datasetType(datasetType)
                                        .build();
                }

                lfvClient.createDataset(createDatasetRequest);

                DatasetDescription datasetDescription = null;

                boolean finished = false;

                // Wait until datase is created, or failure occurs.
                while (!finished) {

                        datasetDescription = describeDataset(lfvClient, projectName, datasetType);

                        switch (datasetDescription.status()) {
                                case CREATE_COMPLETE:
                                        logger.log(Level.INFO, "{0}dataset created for project {1}",
                                                        new Object[] { datasetType, projectName });
                                        finished = true;
                                        break;
                                case CREATE_IN_PROGRESS:
                                        logger.log(Level.INFO, "{0} dataset creating for project {1}",
                                                        new Object[] { datasetType, projectName });

                                        TimeUnit.SECONDS.sleep(5);

                                        break;

                                case CREATE_FAILED:
                                        logger.log(Level.SEVERE,
                                                        "{0} dataset creation failed for project {1}. Error {2}",
                                                        new Object[] { datasetType, projectName,
                                                                        datasetDescription.statusAsString() });
                                        finished = true;
                                        break;
                                default:
                                        logger.log(Level.SEVERE, "{0} error when creating {1} dataset for project {2}",
                                                        new Object[] { datasetType, projectName,
                                                                        datasetDescription.statusAsString() });
                                        finished = true;
                                        break;

                        }
                }

                logger.log(Level.INFO, "Dataset info. Status: {0}\n Message: {1} }",
                                new Object[] { datasetDescription.statusAsString(),
                                                datasetDescription.statusMessage() });

                return datasetDescription;

        }

        /**
         * Gets the JSON lines from an Amazon Lookout for Vision dataset. You can filter
         * on various fields. Specify null
         * to not filter a field.
         * 
         * @param lfvClient          An Amazon Lookout for Vision client.
         * @param projectName        The name of the project that contains the dataset
         *                           you want to list.
         * @param datasetType        The type of the dataset in the project that you
         *                           want to list.
         * @param sourceRef          Filter by the source-ref field of the JSON line.
         * @param classification     Filter by the classification field of the JSON
         *                           line.
         * @param labeled            Filter by whether or not the image in an JSON line
         *                           is labeled.
         * @param beforeCreationDate Filter out JSON lines where the creation-date field
         *                           of the JSON line is after the specified date.
         * @param afterCreationDate  Filter out JSON lines where the creation-date field
         *                           of the JSON line is before the specified date.
         * @return List<String> A list of matching JSON lines.
         */
        public static List<String> listDatasetEntries(LookoutVisionClient lfvClient, String projectName,
                        String datasetType, String sourceRef, String classification, boolean labeled,
                        Instant beforeCreationDate, Instant afterCreationDate) throws LookoutVisionException {

                logger.log(Level.INFO, "Getting JSON Lines:");
                ListDatasetEntriesRequest listDatasetEntriesRequest = ListDatasetEntriesRequest.builder()
                                .projectName(projectName)
                                .datasetType(datasetType)
                                .sourceRefContains(sourceRef)
                                .labeled(labeled)
                                .anomalyClass(classification)
                                .afterCreationDate(afterCreationDate)
                                .beforeCreationDate(beforeCreationDate)
                                .maxResults(100)
                                .build();

                List<String> jsonLines = new ArrayList<>();

                ListDatasetEntriesIterable datasetEntries = lfvClient
                                .listDatasetEntriesPaginator(listDatasetEntriesRequest);

                datasetEntries.stream().flatMap(r -> r.datasetEntries().stream())
                                .forEach(jsonLines::add);

                logger.log(Level.INFO, "Finished getting JSON lines.");

                return jsonLines;

        }

        /**
         * Deletes the train or test dataset in an Amazon Lookout for Vision project.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project in which you want to delete a
         *                    dataset.
         * @param datasetType The type of the dataset that you want to delete (train or
         *                    test).
         * @return Nothing.
         */
        public static void deleteDataset(LookoutVisionClient lfvClient, String projectName, String datasetType)
                        throws LookoutVisionException {

                logger.log(Level.INFO, "Deleting {0} dataset for project {1}",
                                new Object[] { datasetType, projectName });

                DeleteDatasetRequest deleteDatasetRequest = DeleteDatasetRequest.builder()
                                .projectName(projectName)
                                .datasetType(datasetType)
                                .build();

                lfvClient.deleteDataset(deleteDatasetRequest);

                logger.log(Level.INFO, "Deleted {0} dataset for project {1}",
                                new Object[] { datasetType, projectName });

        }

        /**
         * Gets the description for a Amazon Lookout for Vision dataset.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project in which you want to describe a
         *                    dataset.
         * @param datasetType The type of the dataset that you want to describe (train
         *                    or test).
         * @return DatasetDescription A description of the dataset.
         */
        public static DatasetDescription describeDataset(LookoutVisionClient lfvClient,
                        String projectName,
                        String datasetType) throws LookoutVisionException {

                logger.log(Level.INFO, "Describing {0} dataset for project {1}",
                                new Object[] { datasetType, projectName });

                DescribeDatasetRequest describeDatasetRequest = DescribeDatasetRequest.builder()
                                .projectName(projectName)
                                .datasetType(datasetType)
                                .build();

                DescribeDatasetResponse describeDatasetResponse = lfvClient.describeDataset(describeDatasetRequest);
                DatasetDescription datasetDescription = describeDatasetResponse.datasetDescription();

                logger.log(Level.INFO, "Project: {0}\n"
                                + "Created: {1}\n"
                                + "Type: {2}\n"
                                + "Total: {3}\n"
                                + "Labeled: {4}\n"
                                + "Normal: {5}\n"
                                + "Anomalous: {6}\n",
                                new Object[] {
                                                datasetDescription.projectName(),
                                                datasetDescription.creationTimestamp(),
                                                datasetDescription.datasetType(),
                                                datasetDescription.imageStats().total().toString(),
                                                datasetDescription.imageStats().labeled().toString(),
                                                datasetDescription.imageStats().normal().toString(),
                                                datasetDescription.imageStats().anomaly().toString(),
                                });

                return datasetDescription;

        }

                /**
         * Updates an Amazon Lookout for Vision dataset from a manifest file.
         * Returns after Lookout for Vision updates the dataset.
         * 
         * @param lfvClient    An Amazon Lookout for Vision client.
         * @param projectName  The name of the project in which you want to update a
         *                     dataset.
         * @param datasetType  The type of the dataset that you want to update (train or
         *                     test).
         * @param manifestFile The name and location of a local manifest file that you want to
         * use to update the dataset.
         * @return DatasetStatus The status of the updated dataset.
         */

        public static DatasetStatus updateDatasetEntries(LookoutVisionClient lfvClient, String projectName,
                        String datasetType, String updateFile) throws FileNotFoundException, LookoutVisionException,
                        InterruptedException {

                logger.log(Level.INFO, "Updating {0} dataset for project {1}",
                                new Object[] { datasetType, projectName });

                InputStream sourceStream = new FileInputStream(updateFile);
                SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

                UpdateDatasetEntriesRequest updateDatasetEntriesRequest = UpdateDatasetEntriesRequest.builder()
                                .projectName(projectName)
                                .datasetType(datasetType)
                                .changes(sourceBytes)
                                .build();

                lfvClient.updateDatasetEntries(updateDatasetEntriesRequest);

                boolean finished = false;
                DatasetStatus status = null;

                // Wait until update completes

                do {

                        DescribeDatasetRequest describeDatasetRequest = DescribeDatasetRequest.builder()
                                        .projectName(projectName)
                                        .datasetType(datasetType)
                                        .build();
                        DescribeDatasetResponse describeDatasetResponse = lfvClient
                                        .describeDataset(describeDatasetRequest);

                        DatasetDescription datasetDescription = describeDatasetResponse.datasetDescription();

                        status = datasetDescription.status();

                        switch (status) {

                                case UPDATE_COMPLETE:
                                        logger.log(Level.INFO, "{0} Dataset updated for project {1}.",
                                                        new Object[] { datasetType, projectName });
                                        finished = true;
                                        break;

                                case UPDATE_IN_PROGRESS:

                                        logger.log(Level.INFO, "{0} Dataset update for project {1} in progress.",
                                                        new Object[] { datasetType, projectName });
                                        TimeUnit.SECONDS.sleep(5);

                                        break;

                                case UPDATE_FAILED_ROLLBACK_IN_PROGRESS:
                                        logger.log(Level.SEVERE,
                                                        "{0} Dataset update failed for project {1}. Rolling back",
                                                        new Object[] { datasetType, projectName });

                                        TimeUnit.SECONDS.sleep(5);

                                        break;

                                case UPDATE_FAILED_ROLLBACK_COMPLETE:
                                        logger.log(Level.SEVERE,
                                                        "{0} Dataset update failed for project {1}. Rollback completed.",
                                                        new Object[] { datasetType, projectName });
                                        finished = true;
                                        break;

                                default:
                                        logger.log(Level.SEVERE,
                                                        "{0} Dataset update failed for project {1}. Unexpected error returned.",
                                                        new Object[] { datasetType, projectName });
                                        finished = true;

                        }

                } while (!finished);

                return status;

        }

}

// snippet-end:[lookoutvision.java2.datasets.complete]
