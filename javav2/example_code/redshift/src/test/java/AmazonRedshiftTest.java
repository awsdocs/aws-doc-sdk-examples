// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.redshift.*;
import com.example.scenario.RedshiftScenario;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
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

    private static RedshiftDataClient redshiftDataClient;
    private static String clusterId = "";

    private static String fileNameSc = "";

    private static String userName = "";

    private static String userPassword = "" ;

    private static String databaseName = "" ;

    @BeforeAll
    public static void setUp() {
        redshiftClient = RedshiftClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        redshiftDataClient = RedshiftDataClient.builder()
            .region(Region.US_EAST_1)
            .build();

        Random rand = new Random();
        int randomNum = rand.nextInt((10000 - 1) + 1) + 1;
        databaseName = "dev" ;

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        clusterId = values.getClusterId() +randomNum;
        userName = values.getUserName();
        userPassword = values.getPassword();
        fileNameSc = values.getFileName();

        // Uncomment this code block if you prefer using a config.properties file to
        // retrieve AWS values required for these tests.
        /*
        try (InputStream input = AmazonRedshiftTest.class.getClassLoader().getResourceAsStream("config.properties"))
        {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            clusterId =  prop.getProperty("clusterId") +randomNum;
            userName = prop.getProperty("userName");
            userPassword = prop.getProperty("userPassword");
            fileNameSc = prop.getProperty("jsonFilePath");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void helloRedshift() {
        assertDoesNotThrow(() -> HelloRedshift.listClustersPaginator(redshiftClient));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void findReservedNodeOffer() {
        assertDoesNotThrow(() -> FindReservedNodeOffer.listReservedNodes(redshiftClient));
        assertDoesNotThrow(() -> FindReservedNodeOffer.findReservedNodeOffer(redshiftClient));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testScenario() throws InterruptedException, IOException {
        RedshiftScenario.createCluster(redshiftClient, clusterId, userName, userPassword);
        RedshiftScenario.waitForClusterReady(redshiftClient, clusterId);
        RedshiftScenario.createDatabase(redshiftDataClient, clusterId, databaseName, userName);
        RedshiftScenario.createTable(redshiftDataClient, clusterId, databaseName, userName);
        RedshiftScenario.popTable(redshiftDataClient, clusterId, databaseName, userName, fileNameSc, 50);
        String sqlYear = "SELECT * FROM Movies WHERE year = 2012 ;" ;
        String id = RedshiftScenario.queryMoviesByYear(redshiftDataClient, databaseName, userName, sqlYear, clusterId);
        RedshiftScenario.checkStatement(redshiftDataClient, id);
        TimeUnit.SECONDS.sleep(30);
        RedshiftScenario.getResults(redshiftDataClient, id);
        RedshiftScenario.listAllDatabases(redshiftDataClient, clusterId, userName, databaseName);
        RedshiftScenario.modifyCluster(redshiftClient, clusterId);
        RedshiftScenario.deleteRedshiftCluster(redshiftClient, clusterId);
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
        private String userName;

        private String password;
        private String fileName;

        public String getClusterId() {
            return clusterId;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
