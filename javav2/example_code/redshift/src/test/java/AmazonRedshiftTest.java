/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.redshift.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonRedshiftTest {
    private static RedshiftClient redshiftClient;
    private static String clusterId = "";
    private static String secretName = "";
    private static String eventSourceType = "";

    @BeforeAll
    public static void setUp() throws IOException {
        redshiftClient = RedshiftClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        Random rand = new Random();
        int randomNum = rand.nextInt((10000 - 1) + 1) + 1;

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        clusterId = values.getClusterId() +randomNum;
        secretName = values.getSecretName();
        eventSourceType = values.getEventSourceType();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AmazonRedshiftTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            clusterId = prop.getProperty("clusterId")+randomNum;
            masterUsername = prop.getProperty("masterUsername");
            masterUserPassword = prop.getProperty("masterUserPassword");
            eventSourceType = prop.getProperty("eventSourceType");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }
    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateCluster() {
        Gson gson = new Gson();
        User user = gson.fromJson(String.valueOf(CreateAndModifyCluster.getSecretValues(secretName)), User.class);
        assertDoesNotThrow(() ->CreateAndModifyCluster.createCluster(redshiftClient, clusterId, user.getMasterUsername(), user.getMasterUserPassword()));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void WaitForClusterReady() {
        assertDoesNotThrow(() ->CreateAndModifyCluster.waitForClusterReady(redshiftClient, clusterId));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ModifyClusterReady() {
        assertDoesNotThrow(() ->CreateAndModifyCluster.modifyCluster(redshiftClient, clusterId));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DescribeClusters() {
        assertDoesNotThrow(() ->DescribeClusters.describeRedshiftClusters(redshiftClient));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void FindReservedNodeOffer() {
        assertDoesNotThrow(() ->FindReservedNodeOffer.listReservedNodes(redshiftClient));
        assertDoesNotThrow(() ->FindReservedNodeOffer.findReservedNodeOffer(redshiftClient));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void ListEvents() {
        assertDoesNotThrow(() ->ListEvents.listRedShiftEvents(redshiftClient, clusterId, eventSourceType));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DeleteCluster() throws InterruptedException {
        System.out.println("Wait 20 mins for the resource to become available");
        TimeUnit.MINUTES.sleep(20);
        assertDoesNotThrow(() ->DeleteCluster.deleteRedshiftCluster(redshiftClient, clusterId));
        System.out.println("Test 7 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/red";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/red (an AWS Secrets Manager secret)")
    class SecretValues {
        private String clusterId;
        private String secretName;
        private String eventSourceType;

        public String getClusterId() {
            return clusterId;
        }

        public String getSecretName() {
            return secretName;
        }

        public String getEventSourceType() {
            return eventSourceType;
        }
    }
}

