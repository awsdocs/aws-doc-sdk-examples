// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.secrets.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecretManagerTest {

    private static SecretsManagerClient secretsClient;
    private static String newSecretName = "";
    private static String secretValue = "";
    private static String secretARN = "";
    private static String modSecretValue = "";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {
        Region region = Region.US_EAST_1;
        secretsClient = SecretsManagerClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @Test
    @Order(1)
    public void GetSecretValue() {
        assertDoesNotThrow(() -> GetSecretValue.getValue(secretsClient, "mysecret"));
        System.out.println("Test 1 passed");
    }
}
