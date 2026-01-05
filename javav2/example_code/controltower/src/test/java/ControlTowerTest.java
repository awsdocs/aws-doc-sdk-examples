// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.controltower.HelloControlTower;
import com.example.controltower.scenario.ControlTowerActions;
import com.example.controltower.scenario.ControlTowerScenario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.controlcatalog.model.ControlSummary;
import software.amazon.awssdk.services.controltower.ControlTowerClient;
import software.amazon.awssdk.services.controltower.model.BaselineSummary;
import software.amazon.awssdk.services.controltower.model.LandingZoneSummary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControlTowerTest {
    private static ControlTowerClient controlTowerClient;
    @BeforeAll
    public static void setUp() {
        controlTowerClient = ControlTowerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create("default"))
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
    public void testControlTowerActionsAsync() {
        assertDoesNotThrow(() -> {
            // Create an instance of the async actions class
            ControlTowerActions actions = new ControlTowerActions();

            // SAFE: read-only, no admin role required
            List<LandingZoneSummary> landingZones = actions.listLandingZonesAsync().join();
            List<BaselineSummary> baselines = actions.listBaselinesAsync().join();
            List<ControlSummary> controls = actions.listControlsAsync().join();

            // Simple sanity checks
            assertNotNull(landingZones, "Landing zones list should not be null");
            assertNotNull(baselines, "Baselines list should not be null");
            assertNotNull(controls, "Controls list should not be null");

            System.out.println("Landing Zones:  " + landingZones.size());
            System.out.println("Baselines: " + baselines.size());
            System.out.println("Controls:  " + controls.size());
        });

        System.out.println("Test 2 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testControlTowerScenarioEndToEnd() {
        assertDoesNotThrow(() -> {
            String simulatedInput = String.join("\n",
                    Arrays.asList(
                            "c", "y", "c", "c", "y", "n", "n", "c", "y", "n", "c"
                    )) + "\n";

            InputStream originalIn = System.in;
            PrintStream originalOut = System.out;

            try {
                // Simulate user input
                ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
                System.setIn(testIn);

                // Capture output
                System.setOut(new PrintStream(new ByteArrayOutputStream()));

                // Run the scenario
                ControlTowerScenario.main(new String[]{});

            } finally {
                // Restore original I/O
                System.setIn(originalIn);
                System.setOut(originalOut);
            }
        });

        System.out.println("Test 3 (Control Tower scenario end-to-end) passed");
    }
}