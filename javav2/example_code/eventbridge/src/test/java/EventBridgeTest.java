// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.eventbridge.HelloEventBridge;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventBridgeTest {
    private static final Logger logger = LoggerFactory.getLogger(EventBridgeTest.class);
    private static EventBridgeClient eventBrClient;

    @BeforeAll
    public static void setUp() throws IOException {
        eventBrClient = EventBridgeClient.builder()
                .region(Region.US_WEST_2)
                .build();
    }

    @Test
    @Order(1)
    public void testHelloEventBridge() {
        HelloEventBridge.listBuses(eventBrClient);
        logger.info("Test 1 passed");
    }
}