// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.controltower.ControlTowerActions;
import com.example.controltower.HelloControlTower;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.controltower.ControlTowerClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControlTowerTest {
    private static ControlTowerClient controlTowerClient;

    @BeforeAll
    public static void setUp() {
        controlTowerClient = ControlTowerClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    @Test
    @Order(1)
    public void testHelloService() {
        assertDoesNotThrow(() -> {
            HelloControlTower.helloControlTower(controlTowerClient);
        });
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void testControlTowerActions() {
        assertDoesNotThrow(() -> {
            ControlTowerActions.listLandingZones(controlTowerClient);
            ControlTowerActions.listBaselines(controlTowerClient);
            ControlTowerActions.listEnabledBaselines(controlTowerClient);
            
            // Note: Enable/disable operations require valid ARNs and are not tested here
            // to avoid modifying the actual Control Tower configuration
        });
        System.out.println("Test 2 passed");
    }
}