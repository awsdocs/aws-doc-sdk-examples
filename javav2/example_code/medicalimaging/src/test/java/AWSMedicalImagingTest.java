/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.medicalimaging.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSMedicalImagingTest {
    private static MedicalImagingClient medicalImagingClient;

    private static S3Client s3Client;

    private static String datastoreName = "";
    private static String datastoreID = "728f13a131f748bf8d87a55d5ef6c5af";
    private static String importJobID = "";

    private static String dataAccessRoleArn = "";

    private static String inputS3Uri = "";

    private static String outputS3Uri = "";

    private static List<String> importedImageSets = new ArrayList<>();


    @BeforeAll
    public static void setUp() {

        medicalImagingClient = MedicalImagingClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;

        datastoreName = "java_test_" + randomNum;
        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        dataAccessRoleArn = values.getDataAccessRoleArn();
        inputS3Uri = values.getInputS3Uri();
        outputS3Uri = values.getOutputS3Uri();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
    /*

        try (InputStream input = AWSMedicalImagingTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            dataAccessRoleArn = prop.getProperty("dataAccessRoleArn");
            inputS3Uri= prop.getProperty("inputS3Uri");
            outputS3Uri= prop.getProperty("outputS3Uri");

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
       */
    }

//    @Test
//    @Tag("IntegrationTest")
//    @Order(1)
//    public void createDatastoreTest() {
//        assertDoesNotThrow(() -> datastoreID = CreateDatastore.createMedicalImageDatastore(medicalImagingClient, datastoreName));
//        assertFalse(datastoreID.isEmpty());
//
//        // Sleep for 10 seconds to give time for the data store to be created
//        try {
//            Thread.sleep(10000);
//        } catch (java.lang.InterruptedException e) {
//            System.err.println("Sleep Interrupted");
//        }
//
//        System.out.println("Test 1 passed");
//
//    }
//
//    @Test
//    @Tag("IntegrationTest")
//    @Order(2)
//    public void getDatastoreTest() {
//        final DatastoreProperties[] datastoreProperties = {null};
//        assertDoesNotThrow(() -> datastoreProperties[0] = GetDatastore.getMedicalImageDatastore(medicalImagingClient, datastoreID));
//        assertNotNull(datastoreProperties[0]);
//
//        System.out.println("Test 2 passed");
//
//    }
//
//    @Test
//    @Tag("IntegrationTest")
//    @Order(3)
//    public void listDatastoresTest() {
//        @SuppressWarnings("rawtypes") final List[] dataStoreSummaries = {null};
//        assertDoesNotThrow(() -> dataStoreSummaries[0] = ListDatastores.listMedicalImagingDatastores(medicalImagingClient));
//        assertNotNull(dataStoreSummaries[0]);
//
//        System.out.println("Test 3 passed");
//
//    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void startDicomImportJobTest() {
        // Wait until the data store is active before starting the import job.
        int counter = 0;
        while (counter < 10) {
                final DatastoreProperties[] datastoreProperties = {null};
                assertDoesNotThrow(() -> datastoreProperties[0] = GetDatastore.getMedicalImageDatastore(medicalImagingClient, datastoreID));
            assertDoesNotThrow(() -> Thread.sleep(1000));
            if (datastoreProperties[0].datastoreStatus().toString().equals("ACTIVE")) {
                break;
            }
            counter++;
        }
        assertDoesNotThrow(() -> importJobID = StartDicomImportJob.startDicomImportJob(medicalImagingClient, "java_test_job", datastoreID, dataAccessRoleArn, inputS3Uri, outputS3Uri));
        assertFalse(importJobID.isEmpty());

        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void getDicomImportJobTest() {
        assertFalse(importJobID.isEmpty());
        final DICOMImportJobProperties[] dicomImportJobSummaries = {null};
        assertDoesNotThrow(() -> dicomImportJobSummaries[0] = GetDicomImportJob.getDicomImportJob(medicalImagingClient,
                datastoreID, importJobID));
        assertNotNull(dicomImportJobSummaries[0]);

        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void listDicomImportJobsTest() {
        assertFalse(importJobID.isEmpty());
        @SuppressWarnings("rawtypes") final List[] dicomImportJobSummaries = {null};
        assertDoesNotThrow(() -> dicomImportJobSummaries[0] = ListDicomImportJobs.listDicomImportJobs(medicalImagingClient,
                datastoreID));
        assertNotNull(dicomImportJobSummaries[0]);

        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void searchImageSetsTest() {
        assertFalse(importJobID.isEmpty());

        int counter = 0;
        while (counter < 30) {
            final DICOMImportJobProperties[] dicomImportJobSummaries = {null};
            assertDoesNotThrow(() -> dicomImportJobSummaries[0] = GetDicomImportJob.getDicomImportJob(medicalImagingClient,
                    datastoreID, importJobID));
            assertNotNull(dicomImportJobSummaries[0]);
            System.out.println("job status " + dicomImportJobSummaries[0].jobStatus().toString());
            if (dicomImportJobSummaries[0].jobStatus().toString().equals("COMPLETED")) {
                assertDoesNotThrow(() -> importedImageSets = GetDicomImportJob.getImageSetsForImportJobProperties(s3Client,
                        dicomImportJobSummaries[0]));
                break;
            }
            assertDoesNotThrow(() ->Thread.sleep(1000));
            counter++;
        }
        assertTrue(importedImageSets.size() > 1);

        List<SearchFilter> searchFilters = Collections.singletonList(SearchFilter.builder()
                .operator(Operator.BETWEEN)
                .values(SearchByAttributeValue.builder()
                                .createdAt(Instant.parse("1985-04-12T23:20:50.52Z"))
                                .build(),
                        SearchByAttributeValue.builder()
                                .createdAt(Instant.now())
                                .build())
                .build());

         @SuppressWarnings("rawtypes") final List[] searchResults = {null};
        assertDoesNotThrow(() -> searchResults[0] = SearchImageSets.searchMedicalImagingImageSets(medicalImagingClient,
                datastoreID, searchFilters));
        assertNotNull(searchResults[0]);

        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void getImageSetTest() {
        assertFalse(importedImageSets.isEmpty());

        final GetImageSetResponse[] imageSetResponses = {null};
        assertDoesNotThrow(() -> imageSetResponses[0] = GetImageSet.getMedicalImageSet(medicalImagingClient,
                datastoreID, importedImageSets.get(0), "1"));
        assertNotNull(imageSetResponses[0]);

        System.out.println("Test 5 passed");
    }

//    @Test
//    @Tag("IntegrationTest")
//    @Order(9)
//    public void deleteDatastoreTest() {
//        assertDoesNotThrow(() -> DeleteDatastore.deleteMedicalImagingDatastore(medicalImagingClient, datastoreID));
//        System.out.println("Test 4 passed");
//    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        String secretName = "test/medicalimaging";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
     }

    @Nested
    @DisplayName("A class used to get test values from test/iam (an AWS Secrets Manager secret)")
    class SecretValues {
        private String dataAccessRoleArn;
        private String inputS3Uri;
        private String outputS3Uri;
        public String getDataAccessRoleArn() {
            return dataAccessRoleArn;
        }

        public String getInputS3Uri() {
            return inputS3Uri;
        }

        public String getOutputS3Uri() {
            return outputS3Uri;
        }

        }
}