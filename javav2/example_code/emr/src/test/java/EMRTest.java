// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.example.emr.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EMRTest {
    private static final Logger logger = LoggerFactory.getLogger(EMRTest.class);
    private static EmrClient emrClient;
    private static String jar = "";
    private static String myClass = "";
    private static String keys = "";
    private static String logUri = "";
    private static String name = "";
    private static String jobFlowId = "";
    private static String existingClusterId = "";

    @BeforeAll
    public static void setUp() throws IOException {
        emrClient = EmrClient.builder()
            .region(Region.US_WEST_2)
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        jar = values.getJar();
        myClass = values.getMyClass();
        keys = values.getKeys();
        logUri = values.getLogUri();
        name = values.getName();
        existingClusterId = values.getExistingClusterId();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateClusterTest() {
        jobFlowId = CreateCluster.createAppCluster(emrClient, jar, myClass, keys, logUri, name);
        assertFalse(jobFlowId.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testListClusterTest() {
        assertDoesNotThrow(() -> ListClusters.listAllClusters(emrClient));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateEmrFleetTest() {
        assertDoesNotThrow(() -> CreateEmrFleet.createFleet(emrClient));
        logger.info("Test 3 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testCreateSparkClusterTest() {
        assertDoesNotThrow(() -> CreateSparkCluster.createCluster(emrClient, jar, myClass, keys, logUri, name));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testCreateHiveClusterTest() {
        assertDoesNotThrow(() -> CreateHiveCluster.createCluster(emrClient, jar, myClass, keys, logUri, name));
        logger.info("Test 5 passed");

    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testCustomEmrfsMaterialsTest() {
        assertDoesNotThrow(() -> CustomEmrfsMaterials.createEmrfsCluster(emrClient, jar, myClass, keys, logUri, name));
        logger.info("Test 6 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .build();
        String secretName = "text/emr";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/emr (an AWS Secrets Manager secret)")
    class SecretValues {
        private String existingClusterId;
        private String jar;
        private String myClass;

        private String keys;

        private String name;

        private String logUri;

        public String getLogUri() {
            return logUri;
        }

        public String getJar() {
            return jar;
        }

        public String getMyClass() {
            return myClass;
        }

        public String getKeys() {
            return keys;
        }

        public String getName() {
            return name;
        }

        public String getExistingClusterId() {
            return existingClusterId;
        }
    }
}
