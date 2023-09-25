/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.sage.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SageMakerTest {
    private static SageMakerClient sageMakerClient ;
    private static String image = "";
    private static String modelDataUrl = "";
    private static String executionRoleArn = "";
    private static String modelName = "";
    private static String s3UriData = "";
    private static String s3Uri = "";
    private static String trainingJobName = "";
    private static String roleArn = "";
    private static String s3OutputPath = "";
    private static String channelName = "";
    private static String trainingImage = "";
    private static String existingModel = "";


    @BeforeAll
    public static void setUp() throws IOException {
        Region region = Region.US_WEST_2;
        sageMakerClient = SageMakerClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        image = values.getImage();
        modelDataUrl = values.getModelDataUrl();
        executionRoleArn = values.getExecutionRoleArn();
        modelName = values.getModelName()+ java.util.UUID.randomUUID();
        s3UriData = values.getS3UriData();
        s3Uri = values.getS3Uri();
        roleArn = values.getRoleArn();
        trainingJobName = values.getTrainingJobName()+ java.util.UUID.randomUUID();
        s3OutputPath = values.getS3OutputPath();
        channelName = values.getChannelName();
        trainingImage = values.getTrainingImage();
        existingModel = values.getModelName();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = SageMakerTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests
            prop.load(input);
            image = prop.getProperty("image");
            modelDataUrl = prop.getProperty("modelDataUrl");
            executionRoleArn = prop.getProperty("executionRoleArn");
            modelName = prop.getProperty("modelName")+ java.util.UUID.randomUUID();
            s3UriData = prop.getProperty("s3UriData");
            s3Uri = prop.getProperty("s3Uri");
            roleArn = prop.getProperty("roleArn");
            trainingJobName = prop.getProperty("trainingJobName")+ java.util.UUID.randomUUID();
            s3OutputPath = prop.getProperty("s3OutputPath");
            channelName = prop.getProperty("channelName");
            trainingImage = prop.getProperty("trainingImage");
            existingModel = prop.getProperty("existingModel");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateModel() {
        assertDoesNotThrow(() ->CreateModel.createSagemakerModel(sageMakerClient, modelDataUrl, image, modelName,executionRoleArn));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateTrainingJob() {
        assertDoesNotThrow(() ->CreateTrainingJob.trainJob(sageMakerClient, s3UriData, s3Uri, trainingJobName, roleArn, s3OutputPath, channelName, trainingImage));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeTrainingJob() {
        assertDoesNotThrow(() ->DescribeTrainingJob.describeTrainJob(sageMakerClient, trainingJobName));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void ListModels() {
        assertDoesNotThrow(() ->ListModels.listAllModels(sageMakerClient));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void ListNotebooks() {
        assertDoesNotThrow(() ->ListNotebooks.listBooks(sageMakerClient));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void ListAlgorithms() {
        assertDoesNotThrow(() ->ListAlgorithms.listAlgs(sageMakerClient));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void ListTrainingJobs() {
        assertDoesNotThrow(() ->ListTrainingJobs.listJobs(sageMakerClient));
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void DeleteModel() {
        assertDoesNotThrow(() ->DeleteModel.deleteSagemakerModel(sageMakerClient, modelName));
        System.out.println("Test 8 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/sagemaker";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/sagemaker (an AWS Secrets Manager secret)")
    class SecretValues {
        private String trainingJobName;
        private String modelName;
        private String image;

        private String modelDataUrl;

        private String executionRoleArn;
        private String s3UriData;

        private String s3Uri;

        private String roleArn;

        private String s3OutputPath;

        private String channelName;

        private String trainingImage;

        public String getTrainingJobName() {
            return trainingJobName;
        }

        public String getModelName() {
            return modelName;
        }

        public String getImage() {
            return image;
        }

        public String getModelDataUrl() {
            return modelDataUrl;
        }

        public String getExecutionRoleArn() {
            return executionRoleArn;
        }

        public String getS3UriData() {
            return s3UriData;
        }

        public String getS3Uri() {
            return s3Uri;
        }

        public String getRoleArn() {
            return roleArn;
        }

        public String getS3OutputPath() {
            return s3OutputPath;
        }

        public String getChannelName() {
            return channelName;
        }

        public String getTrainingImage() {
            return trainingImage;
        }
    }
}

