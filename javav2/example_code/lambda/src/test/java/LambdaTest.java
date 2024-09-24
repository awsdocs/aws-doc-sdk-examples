// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.lambda.GetAccountSettings;
import com.example.lambda.scenario.LambdaScenario;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeAll;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LambdaTest {
    private static LambdaClient awsLambda;
    private static String functionName = "";
    private static String functionNameSc = "";
    private static String filePath = "";
    private static String role = "";
    private static String handler = "";
    private static String bucketName = "";
    private static String key = "";

    @BeforeAll
    public static void setUp() {
        awsLambda = LambdaClient.builder()
                 .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        functionName = values.getFunctionNameSc() + java.util.UUID.randomUUID();
        filePath = values.getFilePath();
        role = values.getRole();
        handler = values.getHandler();
        functionNameSc = values.getFunctionNameSc() + java.util.UUID.randomUUID();
        bucketName = values.getBucketName();
        key = values.getKey();
    }


    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void GetAccountSettings() {
        assertDoesNotThrow(() -> GetAccountSettings.getSettings(awsLambda));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void LambdaScenario() throws InterruptedException {
        String funArn = LambdaScenario.createLambdaFunction(awsLambda, functionNameSc, key, bucketName, role, handler);
        assertFalse(funArn.isEmpty());
        System.out.println("The function ARN is " + funArn);

        // Get the Lambda function.
        System.out.println("Getting the " + functionNameSc + " Lambda function.");
        assertDoesNotThrow(() -> LambdaScenario.getFunction(awsLambda, functionNameSc));

        // List the Lambda functions.
        System.out.println("Listing all functions.");
        assertDoesNotThrow(() -> LambdaScenario.listFunctions(awsLambda));

        System.out.println("*** Invoke the Lambda function.");
        System.out.println("*** Wait for 5 MIN so the resource is available");
        TimeUnit.MINUTES.sleep(5);
        assertDoesNotThrow(() -> LambdaScenario.invokeFunction(awsLambda, functionNameSc));

        System.out.println("*** Update the Lambda function code.");
        assertDoesNotThrow(() -> LambdaScenario.updateFunctionCode(awsLambda, functionNameSc, bucketName, key));

        System.out.println("*** Invoke the Lambda function again with the updated code.");
        System.out.println("*** Wait for 5 MIN so the resource is available");
        TimeUnit.MINUTES.sleep(5);
        assertDoesNotThrow(() -> LambdaScenario.invokeFunction(awsLambda, functionNameSc));

        System.out.println("Delete the AWS Lambda function.");
        assertDoesNotThrow(() -> LambdaScenario.deleteLambdaFunction(awsLambda, functionNameSc));
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        String secretName = "test/lambda";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/lambda (an AWS Secrets Manager secret)")
    class SecretValues {
        private String filePath;
        private String role;
        private String handler;

        private String functionNameSc;

        private String bucketName;

        private String key;
        private String functionName;

        public String getFilePath() {
            return filePath;
        }

        public String getRole() {
            return role;
        }

        public String getHandler() {
            return handler;
        }

        public String getFunctionNameSc() {
            return functionNameSc;
        }

        public String getKey() {
            return key;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getFunctionName() {
            return functionName;
        }
    }
}
