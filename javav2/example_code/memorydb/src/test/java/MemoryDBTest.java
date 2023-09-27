/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.memorydb.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemoryDBTest {
    private static MemoryDbClient memoryDbClient;
    private static String clusterName="";
    private static String nodeType="";
    private static String subnetGroupName="";
    private static String aclName="";
    private static String snapShotName="";

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        memoryDbClient = MemoryDbClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        clusterName = values.getClusterName()+ randomNum;
        nodeType = values.getNodeType();
        subnetGroupName = values.getSubnetGroupName();
        aclName = values.getAclName();
        snapShotName= values.getSnapShotName()+randomNum;

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = MemoryDBTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            clusterName = prop.getProperty("clusterName")+ java.util.UUID.randomUUID();
            nodeType = prop.getProperty("nodeType");
            subnetGroupName = prop.getProperty("subnetGroupName");
            aclName = prop.getProperty("aclName");
            snapShotName= prop.getProperty("snapShotName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createCluster() {
        assertDoesNotThrow(() ->CreateCluster.createSingleCluster(memoryDbClient, clusterName, nodeType, subnetGroupName, aclName));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void describeSpecificCluster() {
        assertDoesNotThrow(() ->DescribeSpecificCluster.checkIfAvailable(memoryDbClient, clusterName));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void createSnapshot() {
        assertDoesNotThrow(() ->CreateSnapshot.createSpecificSnapshot(memoryDbClient, clusterName, snapShotName));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void describeSnapshot() {
        assertDoesNotThrow(() ->DescribeSnapshots.describeALlSnapshots(memoryDbClient, clusterName));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void describeAllClusters() {
        assertDoesNotThrow(() ->DescribeClusters.getClusters(memoryDbClient));
        assertDoesNotThrow(() ->DescribeSpecificCluster.checkIfAvailable(memoryDbClient, clusterName));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void deleteCluster() {
        assertDoesNotThrow(() ->DeleteCluster.deleteSpecificCluster(memoryDbClient, clusterName));
        System.out.println("Test 6 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/memorydb";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/memorydb (an AWS Secrets Manager secret)")
    class SecretValues {
        private String clusterName;
        private String nodeType;
        private String subnetGroupName;

        private String aclName;

        private String snapShotName;

        public String getSnapShotName() {
            return snapShotName;
        }

        public String getClusterName() {
            return clusterName;
        }

        public String getNodeType() {
            return nodeType;
        }

        public String getSubnetGroupName() {
            return subnetGroupName;
        }

        public String getAclName() {
            return aclName;
        }
    }
}



