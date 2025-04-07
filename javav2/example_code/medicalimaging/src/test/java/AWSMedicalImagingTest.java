// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.medicalimaging.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.utils.ImmutableMap;

import java.io.File;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSMedicalImagingTest {
    private static MedicalImagingClient medicalImagingClient;

    private static String datastoreName = "";

    private static String workingDatastoreId = "";

    private static String imageSetId = "";
    private static String imageFrameId = "";

    private static String dataResourceArn = "";
    private static String createdDatastoreId = "";

    private static String importJobId = "";

    @BeforeAll
    public static void setUp() {

        medicalImagingClient = MedicalImagingClient.builder()
                .region(Region.US_WEST_2) // TODO: change back to US-EAST-1
                .build();

        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;
        datastoreName = "java_test_" + randomNum;
    }

    @SuppressWarnings("resource")
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_WEST_2) // TODO: change back to US-EAST-1
                .build();
        String secretName = "test/medicalimaging";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createDatastoreTest() {
        assertDoesNotThrow(() -> createdDatastoreId = CreateDatastore.createMedicalImageDatastore(medicalImagingClient,
                datastoreName));
        assert (!createdDatastoreId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void listDatastoresTest() {
        @SuppressWarnings("rawtypes")
        final List[] dataStoreSummaries = { null };
        assertDoesNotThrow(
                () -> dataStoreSummaries[0] = ListDatastores.listMedicalImagingDatastores(medicalImagingClient));
        assertNotNull(dataStoreSummaries[0]);
        System.out.println("Test 2 passed");
    }
}