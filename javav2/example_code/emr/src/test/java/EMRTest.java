/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import aws.example.emr.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
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
    private static EmrClient emrClient;
    private static String jar = "";
    private static String myClass = "" ;
    private static String keys = "" ;
    private static String logUri = "" ;
    private static String name = "" ;
    private static String jobFlowId = "";
    private static String existingClusterId = "";

    @BeforeAll
    public static void setUp() throws IOException {
        emrClient = EmrClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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
        existingClusterId= values.getExistingClusterId();


        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = EMRTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            // Populate the data members required for all tests.
            prop.load(input);
            jar = prop.getProperty("jar");
            myClass = prop.getProperty("myClass");
            keys = prop.getProperty("keys");
            logUri = prop.getProperty("logUri");
            name = prop.getProperty("name");
            existingClusterId= prop.getProperty("existingClusterId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Order(1)
    public void createClusterTest() {
        jobFlowId = CreateCluster.createAppCluster(emrClient, jar, myClass, keys, logUri, name);
        assertFalse(jobFlowId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void describeClusterTest() {
        assertDoesNotThrow(() ->DescribeCluster.describeMyCluster(emrClient, existingClusterId));
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void listClusterTest() {
        assertDoesNotThrow(() ->ListClusters.listAllClusters(emrClient));
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void createEmrFleetTest() {
        assertDoesNotThrow(() ->CreateEmrFleet.createFleet(emrClient));
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void addStepsTest() {
        assertDoesNotThrow(() -> AddSteps.addNewStep(emrClient, jobFlowId, jar, myClass));
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void createSparkClusterTest(){
        assertDoesNotThrow(() ->CreateSparkCluster.createCluster(emrClient, jar, myClass, keys, logUri, name));
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void createHiveClusterTest() {
        assertDoesNotThrow(() ->CreateHiveCluster.createCluster(emrClient, jar, myClass, keys, logUri, name));
        System.out.println("Test 7 passed");

    }

    @Test
    @Order(8)
    public void customEmrfsMaterialsTest(){
        assertDoesNotThrow(() ->CustomEmrfsMaterials.createEmrfsCluster(emrClient, jar, myClass, keys, logUri, name));
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void terminateJobFlowTest(){
        assertDoesNotThrow(() ->TerminateJobFlow.terminateFlow(emrClient, existingClusterId));
        System.out.println("Test 9 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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

