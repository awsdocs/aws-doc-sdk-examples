/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.medicalimaging.CreateDatastore;
import com.example.medicalimaging.DeleteDatastore;
import com.example.medicalimaging.GetDatastore;
import com.example.medicalimaging.ListDatastores;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.DatastoreProperties;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSMedicalImagingTest {
    private static MedicalImagingClient medicalImagingClient;

    private static String datastoreName = "";
    private static String datastoreID = "";

    @BeforeAll
    public static void setUp() {

        medicalImagingClient = MedicalImagingClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;

        datastoreName = "java_test_" + randomNum;
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createDatastoreTest() {
        assertDoesNotThrow(() -> datastoreID = CreateDatastore.createMedicalImageDatastore(medicalImagingClient, datastoreName));
        assertFalse(datastoreID.isEmpty());

        // Sleep for 10 seconds to give time for the data store to be created
        try {
            Thread.sleep(10000);
        } catch (java.lang.InterruptedException e) {
            System.err.println("Sleep Interrupted");
        }

        System.out.println("Test 1 passed");

    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void getDatastoreTest() {
        final DatastoreProperties[] datastoreProperties = {null};
        assertDoesNotThrow(() -> datastoreProperties[0] = GetDatastore.getMedicalImageDatastore(medicalImagingClient, datastoreID));
        assertNotNull(datastoreProperties[0]);

        System.out.println("Test 2 passed");

    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void listDatastoresTest() {
        @SuppressWarnings("rawtypes") final List[] dataStoreSummaries = {null};
        assertDoesNotThrow(() -> dataStoreSummaries[0] = ListDatastores.listMedicalImagingDatastores(medicalImagingClient));
        assertNotNull(dataStoreSummaries[0]);

        System.out.println("Test 3 passed");

    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void deleteDatastoreTest() {
        assertDoesNotThrow(() -> DeleteDatastore.deleteMedicalImagingDatastore(medicalImagingClient, datastoreID));
        System.out.println("Test 4 passed");
    }
}