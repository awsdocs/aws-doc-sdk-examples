/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.ecs.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
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
    private static  EcsClient ecsClient;
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
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        clusterName = values.getClusterName()+java.util.UUID.randomUUID();
        taskId = values.getTaskId();
        subnet = values.getSubnet();
        securityGroups = values.getSecurityGroups();
        serviceName = values.getServiceName() +java.util.UUID.randomUUID();
        taskDefinition = values.getTaskDefinition();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = EcsTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            clusterName = prop.getProperty("clusterName")+java.util.UUID.randomUUID();
            taskId = prop.getProperty("taskId");
            subnet = prop.getProperty("subnet");
            securityGroups = prop.getProperty("securityGroups");
            serviceName = prop.getProperty("serviceName")+java.util.UUID.randomUUID();
            taskDefinition = prop.getProperty("taskDefinition");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateCluster() {
        clusterARN = CreateCluster.createGivenCluster(ecsClient, clusterName);
        assertFalse(clusterARN.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void ListClusters() {
        assertDoesNotThrow(() ->ListClusters.listAllClusters(ecsClient));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeClusters() {
        assertDoesNotThrow(() ->DescribeClusters.descCluster(ecsClient, clusterARN));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void ListTaskDefinitions() {
        assertDoesNotThrow(() ->ListTaskDefinitions.getAllTasks(ecsClient, clusterARN, taskId));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void CreateService() {
        serviceArn  = CreateService.createNewService(ecsClient, clusterName, serviceName, securityGroups, subnet, taskDefinition);
        assertFalse(serviceArn.isEmpty());
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void UpdateService() throws InterruptedException {
        Thread.sleep(20000);
        assertDoesNotThrow(() ->UpdateService.updateSpecificService(ecsClient, clusterName, serviceArn));
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DeleteService() {
        assertDoesNotThrow(() ->DeleteService.deleteSpecificService(ecsClient, clusterName, serviceArn));
        System.out.println("Test 8 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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

