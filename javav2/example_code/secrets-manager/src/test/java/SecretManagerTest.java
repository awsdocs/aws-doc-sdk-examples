// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.secrets.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecretManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(SecretManagerTest.class);
    private static SecretsManagerClient secretsClient;

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        secretsClient = SecretsManagerClient.builder()
                .region(region)
                .build();
    }

    @Test
    @Order(1)
    public void testGetSecretValue() {
        assertDoesNotThrow(() -> GetSecretValue.getValue(secretsClient, "mysecret"));
        logger.info("Test 1 passed");
    }
}
