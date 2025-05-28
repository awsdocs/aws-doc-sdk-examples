// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import com.example.keyspace.HelloKeyspaces;
import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.keyspaces.KeyspacesClient;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KeyspaceTest {
    private static KeyspacesClient keyClient;
    private static final Logger logger = LoggerFactory.getLogger(KeyspaceTest.class);
    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        keyClient = KeyspacesClient.builder()
                .region(region)
                .build();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testKeyspaceTest() {
        assertDoesNotThrow(() -> HelloKeyspaces.listKeyspaces(keyClient),
            "Failed to list namespaces.");
        logger.info("Test 1 passed");
    }
}
