// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InspectorTests {
    private static Inspector2Client inspector;
    private static InspectorActions inspectorActions;

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
            System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testScenario() {
        assertDoesNotThrow(() -> {
            int maxResults = 10;
            inspectorActions.getAccountStatus(inspector);
            inspectorActions.enableInspector(inspector, null);

            inspectorActions.listFindings(inspector, maxResults, null);
            maxResults = 5;
            inspectorActions.listCoverage(inspector, maxResults);

            inspectorActions.createFilter(inspector, "Suppress low severity findings for demo purposes");
            inspectorActions.listFilters(inspector, 10);
            inspectorActions.listUsageTotals(inspector, null, 10);
            inspectorActions.listCoverageStatistics(inspector);
        });
        System.out.println("Test 2 passed");
    }
}
