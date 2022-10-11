/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/* Unit tests for Amazon Lookout for Vision API examples */

import com.example.lookoutvision.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.DatasetDescription;
import software.amazon.awssdk.services.lookoutvision.model.DatasetStatus;
import software.amazon.awssdk.services.lookoutvision.model.DetectAnomalyResult;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelDescription;
import software.amazon.awssdk.services.lookoutvision.model.ModelMetadata;
import software.amazon.awssdk.services.lookoutvision.model.ModelPackagingDescription;
import software.amazon.awssdk.services.lookoutvision.model.ModelPackagingJobMetadata;
import software.amazon.awssdk.services.lookoutvision.model.ModelPackagingJobStatus;
import software.amazon.awssdk.services.lookoutvision.model.ModelStatus;
import software.amazon.awssdk.services.lookoutvision.model.ProjectMetadata;
import software.amazon.awssdk.services.lookoutvision.model.Tag;

import java.io.*;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LookoutVisionTest {

    private static LookoutVisionClient lfvClient;

    private static String projectName = "";
    private static String modelDescription = "";
    private static String modelVersion = "";
    private static String modelTrainingOutputBucket = "";
    private static String modelTrainingOutputFolder = "";
    private static String photo = "";
    private static String anomalousPhoto = "";
    private static String anomalyLabel = "";
    private static String datasetType = "";
    private static String manifestFile = "";
    private static String jobName = "";
    private static String modelPackageJobJsonFile = "";

    @BeforeAll
    public static void setUp() throws IOException {

        try (InputStream input = LookoutVisionTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Load the properties file.
            prop.load(input);
            projectName = prop.getProperty("projectName");
            modelDescription = prop.getProperty("modelDescription");
            modelTrainingOutputBucket = prop.getProperty("modelTrainingOutputBucket");
            modelTrainingOutputFolder = prop.getProperty("modelTrainingOutputFolder");
            photo = prop.getProperty("photo");
            anomalousPhoto = prop.getProperty("anomalousPhoto");
            anomalyLabel = prop.getProperty("anomalyLabel");
            manifestFile = prop.getProperty("manifestFile");
            // jobName = prop.getProperty("jobName");
            modelPackageJobJsonFile = prop.getProperty("modelPackageJobJsonFile");

            /*
             * To complete tests, the model version must be 1 (Lookout For Vision assigns
             * the version number 1 to the first model trained in a project).
             * The dataset type must be train for training to succeed. The test dataest is
             * automatically created.)
             */
            modelVersion = "1";
            datasetType = "train";

            // Get the lookoutvision client.
            lfvClient = LookoutVisionClient.builder()
                    .build();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSRekognitionService_thenNotNull() {
        assertNotNull(lfvClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void createProjectPanel_thenNotNull() throws IOException, LookoutVisionException {

        ProjectMetadata project = Projects.createProject(lfvClient, projectName);

        assertEquals(projectName, project.projectName());
        System.out.println("Test 2 passed");

    }

    @Test
    @Order(3)
    public void listProjects_thenNotNull() throws IOException, LookoutVisionException {

        List<ProjectMetadata> projects = Projects.listProjects(lfvClient);
        assertNotNull(projects);
        System.out.println("Test 3 passed");

    }

    @Test
    @Order(4)
    public void createEmptyDataset_thenEqual() throws IOException, LookoutVisionException, InterruptedException {

        try {

            // Create empty dataset.
            DatasetDescription dataset = Datasets.createDataset(lfvClient, projectName,
                    datasetType, null, null);

            assertEquals(DatasetStatus.CREATE_COMPLETE, dataset.status());

        } catch (LookoutVisionException e) {

            assertEquals("ResourceNotFoundException", e.awsErrorDetails().errorCode());
            System.out.println("Test 4 passed");
        }

    }

    @Test
    @Order(5)
    public void updateEmptyDataset_thenEquals() throws IOException, LookoutVisionException, InterruptedException {

        // Update dataset with local manifest file.
        DatasetStatus dataset = Datasets.updateDatasetEntries(lfvClient, projectName,
                datasetType, manifestFile);

        assertEquals(DatasetStatus.UPDATE_COMPLETE, dataset);
        System.out.println("Test 5 passed");

    }

    @Test
    @Order(6)
    public void listDatasetEntries_thenNotNull() throws LookoutVisionException {

        String sourceRef = null;
        String classification = null;
        Instant afterCreationDate = null;
        Instant beforeCreationDate = null;
        Boolean labeled = true;

        // Get JSON lines from dataset.
        List<String> jsonLines = Datasets.listDatasetEntries(lfvClient, projectName, datasetType, sourceRef,
                classification, labeled, beforeCreationDate, afterCreationDate);

        assertNotNull(jsonLines);
        System.out.println("Test 6 passed");

    }

    @Test
    @Order(7)
    public void describeDataset_thenEquals() throws IOException, LookoutVisionException {

        // Describe a dataset.

        DatasetDescription datasetDescription = Datasets.describeDataset(lfvClient, projectName, datasetType);

        // Check that a description is returned with the right project name and dataset
        // type.
        assertEquals(datasetDescription.projectName(), projectName);
        assertEquals(datasetDescription.datasetType(), datasetType);
        System.out.println("Test 7 passed");

    }

    @Test
    @Order(8)
    public void createModel_thenEqual() throws IOException, LookoutVisionException, InterruptedException {

        // Create and train the model.
        ModelDescription model = Models.createModel(lfvClient, projectName, modelDescription,
                modelTrainingOutputBucket, modelTrainingOutputFolder);

        assertEquals(ModelStatus.TRAINED, model.status());
        System.out.println("Test 8 passed");

    }

    @Test
    @Order(9)
    public void tagModel_thenNotEquals() throws IOException, LookoutVisionException {

        String key = "TestTagkey1";
        String value = "TestTagKeyValue1";

        Tag tag = Tag.builder()
                .key(key)
                .value(value)
                .build();

        // Describe a model.
        Models.tagModel(lfvClient, projectName, modelVersion,
                key, value);

        List<Tag> tags = Models.listTagsForModel(lfvClient,
                projectName, modelVersion);

        int index = tags.indexOf(tag);

        assertNotEquals(-1, index);

        System.out.println("Test 9 passed");

    }

    @Test
    @Order(10)
    public void listModelTags_thenNotFalse() throws LookoutVisionException {

        // Get JSON lines from dataset.
        List<Tag> tags = Models.listTagsForModel(lfvClient, projectName, modelVersion);

        assertEquals(false, tags.isEmpty());
        System.out.println("Test 10 passed");

    }

    @Test
    @Order(11)
    public void untagModel_thenEquals() throws IOException, LookoutVisionException {

        String key = "TestTagkey1";
        String value = "TestTagKeyValue1";

        Tag tag = Tag.builder()
                .key(key)
                .value(value)
                .build();

        // Describe a model.
        Models.untagModel(lfvClient, projectName, modelVersion,
                key);

        List<Tag> tags = Models.listTagsForModel(lfvClient,
                projectName, modelVersion);

        int index = tags.indexOf(tag);

        assertEquals(-1, index);

        System.out.println("Test 11 passed");

    }

    @Test
    @Order(12)
    public void describeModel_thenNotNull() throws IOException, LookoutVisionException {

        // Describe a model.
        ModelDescription description = Models.describeModel(lfvClient, projectName, modelVersion);

        // Check that a description is returned.
        assertNotNull(description);
        System.out.println("Test 12 passed");

    }

    @Test
    @Order(13)
    public void listModels_thenNotNull() throws IOException, LookoutVisionException {

        // Describe a model.
        List<ModelMetadata> models = Models.listModels(lfvClient, projectName);

        assertNotNull(models);

        System.out.println("Test 13 passed");

    }

    @Test
    @Order(14)
    public void startModel_thenNotEquals() throws IOException, LookoutVisionException, InterruptedException {

        // Describe a model.
        ModelDescription model = Hosting.startModel(lfvClient, projectName, modelVersion, 1);

        assertEquals(ModelStatus.HOSTED, model.status());

        System.out.println("Test 14 passed");

    }

    @Test
    @Order(15)
    public void detectAnomaliesPanel_thenNotNull() throws IOException, LookoutVisionException {

        float minConfidence = (float) 0.5;
    
        // Check normal classification
        DetectAnomalyResult prediction = DetectAnomalies.detectAnomalies(lfvClient, projectName, modelVersion, photo);
        boolean reject = DetectAnomalies.rejectOnClassification(photo, prediction, minConfidence);
        assertEquals(Boolean.FALSE, reject);

        // Check anomalous classification
        prediction = DetectAnomalies.detectAnomalies(lfvClient, projectName, modelVersion, anomalousPhoto);
        reject = DetectAnomalies.rejectOnClassification(anomalousPhoto, prediction, minConfidence);
        assertEquals(Boolean.TRUE, reject);

        // Check at least 1 anomaly exists
        reject = DetectAnomalies.rejectOnAnomalyTypeCount(anomalousPhoto, prediction, minConfidence, 0);
        assertEquals(Boolean.TRUE, reject);

        // Check coverage for an anomaly is at least 0.01.
        reject = DetectAnomalies.rejectOnCoverage(anomalousPhoto, prediction, minConfidence, anomalyLabel,
                (float) 0.01);
        assertEquals(Boolean.TRUE, reject);

        System.out.println("Test 15 passed");

    }

    @Test
    @Order(16)
    public void showAnomaliesPanel_thenNotNull() throws IOException, LookoutVisionException {

        ShowAnomalies panel = new ShowAnomalies(lfvClient, projectName, modelVersion, photo);
        assertNotNull(panel);

        System.out.println("Test 16 passed");

    }

    @Test
    @Order(17)
    public void stopModel_thenEquals() throws IOException, LookoutVisionException, InterruptedException {

        // Describe a model.
        ModelDescription model = Hosting.stopModel(lfvClient, projectName, modelVersion);

        assertEquals(ModelStatus.TRAINED, model.status());

        System.out.println("Test 17 passed");

    }

    @Test
    @Order(18)
    public void startModelPackagingJob_thenEqual() throws IOException, LookoutVisionException, InterruptedException {

        // Create the model packaging job.
        ModelPackagingDescription packageDescription = EdgePackages.startModelPackagingJob(lfvClient, projectName,
                modelPackageJobJsonFile);

        jobName = packageDescription.jobName();

        assertEquals(ModelPackagingJobStatus.SUCCEEDED, packageDescription.status());

        System.out.println("Test 18 passed");

    }

    @Test
    @Order(19)
    public void listModelPackagingJobs_thenEquals() throws IOException, LookoutVisionException, InterruptedException {

        // List model packaging jobs.
        List<ModelPackagingJobMetadata> packagingJobs = EdgePackages.listModelPackagingJobs(lfvClient, projectName);

        // When run in sequence, jobName is picked up from
        // startModelPackagingJob_thenEqual(). Otherwise, specify a value for jobName.

        assertEquals(jobName, packagingJobs.get(0).jobName());

        System.out.println("Test 19 passed");

    }

    @Test
    @Order(20)
    public void describeModelPackagingJob_thenNotNull() throws IOException, LookoutVisionException {

        // Describe a model.

        // When run in sequence, jobName is picked up from
        // startModelPackagingJob_thenEqual(). Otherwise, specify a value for jobName.
        ModelPackagingDescription description = EdgePackages.describeModelPackagingJob(lfvClient, projectName, jobName);

        // Check that a description is returned.
        assertNotNull(description);
        System.out.println("Test 20 passed");

    }

    @Test
    @Order(21)
    public void deleteDataset_thenNotFound() throws IOException,
            LookoutVisionException {

        try {

            // Delete a dataset.
            Datasets.deleteDataset(lfvClient, projectName, datasetType);
            Datasets.describeDataset(lfvClient, projectName, datasetType);

        } catch (LookoutVisionException e) {

            assertEquals("ResourceNotFoundException", e.awsErrorDetails().errorCode());
            System.out.println("Test 21 passed");
        }

    }

    @Test
    @Order(22)
    public void deleteModel_thenNotFound() throws IOException,
            LookoutVisionException, InterruptedException {

        try {

            // Delete a model.
            Models.deleteModel(lfvClient, projectName, modelVersion);
            Models.describeModel(lfvClient, projectName, modelVersion);

        } catch (LookoutVisionException e) {

            assertEquals("ResourceNotFoundException", e.awsErrorDetails().errorCode());
            System.out.println("Test 22 passed");
        }

    }

    @Test
    @Order(23)
    public void deleteProject_thenNotFound() throws IOException,
            LookoutVisionException, InterruptedException {

        try {
            Projects.deleteProject(lfvClient, projectName);
            Projects.describeProject(lfvClient, projectName);
        } catch (LookoutVisionException e) {

            assertEquals("ResourceNotFoundException", e.awsErrorDetails().errorCode());
            System.out.println("Test 23 passed");
        }

    }

}
