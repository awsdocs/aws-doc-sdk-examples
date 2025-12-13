// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.java.inspector.InspectorScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.java.inspector.HelloInspector;
import com.java.inspector.InspectorActions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.inspector2.Inspector2Client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InspectorTests {
    private static Inspector2Client inspector;
    private static InspectorActions inspectorActions;
    private static final Logger logger = LoggerFactory.getLogger(InspectorTests.class);
    @BeforeAll
    public static void setUp() {
        inspector = Inspector2Client.builder()
                .region(Region.US_EAST_1)
                .build() ;

        inspectorActions = new InspectorActions();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testHelloService() {
        assertDoesNotThrow(() -> {
            HelloInspector.checkAccountStatus(inspector);
            HelloInspector.listRecentFindings(inspector);
            HelloInspector.showUsageTotals(inspector);
        });
        logger.info("Test 1 passed");
    }


    /**
     * Integration test for InspectorActions Async methods.
     *
     * This test validates that all async action methods complete successfully and
     * return expected values (like filter ARN).
     *
     * Note that it will fail the test if any .join() throws a CompletionException.
     */

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testInspectorActionsIntegration() {
        assertDoesNotThrow(() -> {
            int maxResults = 10;

            String filterName = "suppress-low-severity-" + System.currentTimeMillis();

            inspectorActions.getAccountStatusAsync().join();

            inspectorActions.enableInspectorAsync(null).join();

            String allFindings = inspectorActions.listLowSeverityFindingsAsync().join();
            // Check if any findings were returned
            if (allFindings == null || allFindings.startsWith("No LOW severity findings")) {
                logger.info("No LOW severity findings available. Skipping details lookup.");
            } else {
                String[] arns = allFindings.split("\\r?\\n");
                String lastArn = arns[arns.length - 1];

                // Fetch details safely
                String details = inspectorActions.getFindingDetailsAsync(lastArn).join();
                logger.info("Details for last LOW severity finding:\n{}", details);
            }


            maxResults = 5;
            inspectorActions.listCoverageAsync(maxResults).join();

            String filterARN = inspectorActions.createLowSeverityFilterAsync(filterName,"Suppress low severity findings for demo purposes").join();

            // Assert it returned a valid ARN
            assertNotNull(filterARN, "Filter ARN should not be null");
            assertFalse(filterARN.isBlank(), "Filter ARN should not be empty");

            inspectorActions.listFiltersAsync(10).join();

            inspectorActions.listUsageTotalsAsync(null, 10).join();

            inspectorActions.listCoverageStatisticsAsync().join();

            inspectorActions.deleteFilterAsync(filterARN).join();
        });

        logger.info("Test 2 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testInspectorScenarioEndToEnd() {
        assertDoesNotThrow(() -> {

            // The scenario calls scanner.nextLine() repeatedly.
            // We simulate user input by providing many "c" lines.
            String simulatedInput = String.join("\n",
                    Collections.nCopies(20, "c")) + "\n";

            InputStream originalIn = System.in;
            PrintStream originalOut = System.out;

            try {
                // Redirect System.in to simulated input
                ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
                System.setIn(testIn);

                // Capture System.out so logs don’t spam the console
                System.setOut(new PrintStream(new ByteArrayOutputStream()));

                // Run the scenario
                InspectorScenario.main(new String[]{});

            } finally {
                // Restore original I/O streams
                System.setIn(originalIn);
                System.setOut(originalOut);
            }
        });

        logger.info("Test 3 (Scenario end-to-end) passed");
    }
}