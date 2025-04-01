// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.ecs.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EcsTest {
    private static final Logger logger = LoggerFactory.getLogger(EcsTest.class);
    private static EcsClient ecsClient;
    private static String clusterName = "";
    private static String clusterARN = "";
    private static String taskId = "";
    private static String securityGroups = "";
    private static String subnet = "";
    private static String serviceName = "";
    private static String serviceArn = "";
    private static String taskDefinition = "";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        ecsClient = EcsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        clusterName = values.getClusterName() + java.util.UUID.randomUUID();
        taskId = values.getTaskId();
        subnet = values.getSubnet();
        securityGroups = values.getSecurityGroups();
        serviceName = values.getServiceName() + java.util.UUID.randomUUID();
        taskDefinition = values.getTaskDefinition();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateCluster() {
        clusterARN = CreateCluster.createGivenCluster(ecsClient, clusterName);
        assertFalse(clusterARN.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testListClusters() {
        assertDoesNotThrow(() -> ListClusters.listAllClusters(ecsClient));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeClusters() {
        assertDoesNotThrow(() -> DescribeClusters.descCluster(ecsClient, clusterARN));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testListTaskDefinitions() {
        assertDoesNotThrow(() -> ListTaskDefinitions.getAllTasks(ecsClient, clusterARN, taskId));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testCreateService() {
        serviceArn = CreateService.createNewService(ecsClient, clusterName, serviceName, securityGroups, subnet,
                taskDefinition);
        assertFalse(serviceArn.isEmpty());
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testUpdateService() throws InterruptedException {
        Thread.sleep(20000);
        assertDoesNotThrow(() -> UpdateService.updateSpecificService(ecsClient, clusterName, serviceArn));
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testDeleteService() {
        assertDoesNotThrow(() -> DeleteService.deleteSpecificService(ecsClient, clusterName, serviceArn));
        logger.info("Test 7 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/ecs";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/ecs (an AWS Secrets Manager secret)")
    class SecretValues {
        private String clusterName;
        private String securityGroups;
        private String subnet;

        private String taskId;

        private String serviceName;

        private String taskDefinition;

        public String getClusterName() {
            return clusterName;
        }

        public String getSecurityGroups() {
            return securityGroups;
        }

        public String getSubnet() {
            return subnet;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getTaskDefinition() {
            return taskDefinition;
        }
    }
}
