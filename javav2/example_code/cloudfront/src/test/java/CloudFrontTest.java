/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import com.example.cloudfront.*;
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
public class CloudFrontTest {

    private static CloudFrontClient cloudFrontClient ;
    private static String functionName = "";
    private static String filePath = "";
    private static String funcARN = "";
    private static String eTagVal = "";
    private static String id = "";
    @BeforeAll
    public static void setUp() {
        cloudFrontClient = CloudFrontClient.builder()
            .region(Region.AWS_GLOBAL)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        functionName = values.getFunctionName();
        filePath = values.getFilePath();
        id = values.getId();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = CloudFrontTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            functionName = prop.getProperty("functionName");
            filePath= prop.getProperty("filePath");
            id = prop.getProperty("id");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateFunction() {
        funcARN =  CreateFunction.createNewFunction(cloudFrontClient, functionName, filePath);
        assertFalse(funcARN.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void DescribeFunction() {
        eTagVal = DescribeFunction.describeSinFunction(cloudFrontClient, functionName);
        assertFalse(eTagVal.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ListFunctions(){
        assertDoesNotThrow(() ->ListFunctions.listAllFunctions(cloudFrontClient));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
   public void GetDistrubutions() {
        assertDoesNotThrow(() ->GetDistrubutions.getCFDistrubutions(cloudFrontClient));
        System.out.println("Test 4 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
   public void ModifyDistrution() {
        assertDoesNotThrow(() ->ModifyDistribution.modDistribution(cloudFrontClient, id));
        System.out.println("Test 5 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
   public void DeleteFunction(){
        assertDoesNotThrow(() ->DeleteFunction.deleteSpecificFunction(cloudFrontClient, functionName, eTagVal));
       System.out.println("Test 6 passed");
    }

   private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/cloudfront";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/cloudfront (an AWS Secrets Manager secret)")
    class SecretValues {
        private String functionName;
        private String filePath;
        private String id;

        public String getFunctionName() {
            return functionName;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getId() {
            return id;
        }
    }
}

