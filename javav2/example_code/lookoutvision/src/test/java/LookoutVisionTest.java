// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* Unit tests for Amazon Lookout for Vision API examples */

import com.example.lookoutvision.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.DatasetDescription;
import software.amazon.awssdk.services.lookoutvision.model.DatasetStatus;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ModelMetadata;
import software.amazon.awssdk.services.lookoutvision.model.ProjectMetadata;
import java.io.*;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LookoutVisionTest {
    private static LookoutVisionClient lfvClient;
    private static String projectName = "";
    private static String modelVersion = "";
    private static String datasetType = "";

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
            // jobName = prop.getProperty("jobName");

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
    @Tag("IntegrationTest")
    @Order(1)
    public void createProjectPanel_thenNotNull() throws IOException, LookoutVisionException {
        ProjectMetadata project = Projects.createProject(lfvClient, projectName);
        assertEquals(projectName, project.projectName());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void listProjects_thenNotNull() throws IOException, LookoutVisionException {
        List<ProjectMetadata> projects = Projects.listProjects(lfvClient);
        assertNotNull(projects);
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void createEmptyDataset_thenEqual() throws IOException, LookoutVisionException, InterruptedException {
        try {
            // Create empty dataset.
            DatasetDescription dataset = Datasets.createDataset(lfvClient, projectName,
                    datasetType, null, null);

            assertEquals(DatasetStatus.CREATE_COMPLETE, dataset.status());

        } catch (LookoutVisionException e) {
            assertEquals("ResourceNotFoundException", e.awsErrorDetails().errorCode());
            System.out.println("Test 3 passed");
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
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
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void describeDataset_thenEquals() throws IOException, LookoutVisionException {
        // Describe a dataset.

        DatasetDescription datasetDescription = Datasets.describeDataset(lfvClient, projectName, datasetType);
        // Check that a description is returned with the right project name and dataset
        // type.
        assertEquals(datasetDescription.projectName(), projectName);
        assertEquals(datasetDescription.datasetType(), datasetType);
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void listModels_thenNotNull() throws IOException, LookoutVisionException {
        // Describe a model.
        List<ModelMetadata> models = Models.listModels(lfvClient, projectName);
        assertNotNull(models);
        System.out.println("Test 6 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void deleteDataset_thenNotFound() throws IOException,
            LookoutVisionException {
        try {
            // Delete a dataset.
            Datasets.deleteDataset(lfvClient, projectName, datasetType);
            Datasets.describeDataset(lfvClient, projectName, datasetType);

        } catch (LookoutVisionException e) {

            assertEquals("ResourceNotFoundException", e.awsErrorDetails().errorCode());
            System.out.println("Test 7 passed");
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void deleteModel_thenNotFound() throws IOException,
            LookoutVisionException, InterruptedException {
        try {
            // Delete a model.
            Models.deleteModel(lfvClient, projectName, modelVersion);
            Models.describeModel(lfvClient, projectName, modelVersion);
        } catch (LookoutVisionException e) {
            assertEquals("ResourceNotFoundException", e.awsErrorDetails().errorCode());
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void deleteProject_thenNotFound() throws IOException,
            LookoutVisionException, InterruptedException {
        try {
            Projects.deleteProject(lfvClient, projectName);
            Projects.describeProject(lfvClient, projectName);
        } catch (LookoutVisionException e) {
            assertEquals("ResourceNotFoundException", e.awsErrorDetails().errorCode());
            System.out.println("Test 9 passed");
        }
    }
}
