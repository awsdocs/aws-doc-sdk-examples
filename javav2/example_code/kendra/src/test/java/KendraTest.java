/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.kendra.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class KendraTest {

    private static KendraClient kendra;
    private static String indexName = "";
    private static String indexDescription = "";
    private static String indexRoleArn = "";
    private static String indexId = "";
    private static String s3BucketName = "";
    private static String dataSourceName = "";
    private static String dataSourceDescription = "";
    private static String dataSourceRoleArn = "";
    private static String dataSourceId = "";
    private static String text = "";

    @BeforeAll
    public static void setUp() {
        kendra = KendraClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        indexName = values.getIndexName()+ java.util.UUID.randomUUID();
        indexRoleArn = values.getIndexRoleArn();
        indexDescription = values.getIndexDescription();
        s3BucketName = values.getS3BucketName();
        dataSourceName = values.getDataSourceName();
        dataSourceDescription = values.getDataSourceDescription();
        dataSourceRoleArn = values.getDataSourceRoleArn();
        text = values.getText();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = KendraTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Load a properties file from the class path.
            prop.load(input);

            // Populate the data members required for all tests.
            indexName = prop.getProperty("indexName")+ java.util.UUID.randomUUID();
            indexRoleArn = prop.getProperty("indexRoleArn");
            indexDescription = prop.getProperty("indexDescription");
            s3BucketName = prop.getProperty("s3BucketName");
            dataSourceName = prop.getProperty("dataSourceName");
            dataSourceDescription = prop.getProperty("dataSourceDescription");
            dataSourceRoleArn = prop.getProperty("dataSourceRoleArn");
            text = prop.getProperty("text");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateIndex() {
        indexId = CreateIndexAndDataSourceExample.createIndex(kendra, indexDescription, indexName, indexRoleArn);
        assertFalse(indexId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateDataSource() {
        dataSourceId = CreateIndexAndDataSourceExample.createDataSource(kendra, s3BucketName, dataSourceName, dataSourceDescription, indexId, dataSourceRoleArn);
        assertFalse(dataSourceId.isEmpty());
        System.out.println("Test 2 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void SyncDataSource() {
        assertDoesNotThrow(() ->CreateIndexAndDataSourceExample.startDataSource(kendra, indexId, dataSourceId));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void ListSyncJobs() {
        assertDoesNotThrow(() ->ListDataSourceSyncJobs.listSyncJobs(kendra, indexId, dataSourceId));
        System.out.println("Test 4 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void QueryIndex() {
        assertDoesNotThrow(() ->QueryIndex.querySpecificIndex(kendra, indexId, text));
        System.out.println("Test 5 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void DeleteDataSource() {
        assertDoesNotThrow(() ->DeleteDataSource.deleteSpecificDataSource(kendra, indexId, dataSourceId));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DeleteIndex() {
        assertDoesNotThrow(() ->DeleteIndex.deleteSpecificIndex(kendra, indexId));
        System.out.println("Test 7 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/kendra";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/kendra (an AWS Secrets Manager secret)")
    class SecretValues {
        private String indexName;
        private String dataSourceName;
        private String indexDescription;

        private String indexRoleArn;

        private String s3BucketName;

        private String dataSourceDescription;

        private String text;

        private String dataSourceRoleArn;

        public String getIndexName() {
            return indexName;
        }

        public String getDataSourceName() {
            return dataSourceName;
        }

        public String getIndexDescription() {
            return indexDescription;
        }

        public String getIndexRoleArn() {
            return indexRoleArn;
        }

        public String getS3BucketName() {
            return s3BucketName;
        }

        public String getDataSourceDescription() {
            return dataSourceDescription;
        }

        public String getText() {
            return text;
        }

        public String getDataSourceRoleArn() {
            return dataSourceRoleArn;
        }
    }
}
