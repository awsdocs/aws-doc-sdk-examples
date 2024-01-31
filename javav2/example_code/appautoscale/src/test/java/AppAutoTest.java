// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.appautoscale.DisableDynamoDBAutoscaling;
import com.example.appautoscale.EnableDynamoDBAutoscaling;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.applicationautoscaling.ApplicationAutoScalingClient;
import software.amazon.awssdk.services.applicationautoscaling.model.ScalableDimension;
import software.amazon.awssdk.services.applicationautoscaling.model.ServiceNamespace;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppAutoTest {

    private static ApplicationAutoScalingClient appAutoScalingClient;
    private static String tableId = "" ;
    private static String roleARN = "" ;
    private static String policyName = "" ;

    ServiceNamespace ns = ServiceNamespace.DYNAMODB;
    ScalableDimension tableWCUs = ScalableDimension.DYNAMODB_TABLE_WRITE_CAPACITY_UNITS;

    @BeforeAll
    public static void setUp() throws IOException {
        appAutoScalingClient = ApplicationAutoScalingClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try (InputStream input = AppAutoTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            tableId = prop.getProperty("tableId");
            roleARN = prop.getProperty("roleARN");
            policyName = prop.getProperty("policyName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testEnableDynamoDBAutoscaling() {
        assertDoesNotThrow(() -> EnableDynamoDBAutoscaling.registerScalableTarget(appAutoScalingClient, tableId, roleARN, ns, tableWCUs),
            "Failed to register scalable target.");
        assertDoesNotThrow(() -> EnableDynamoDBAutoscaling.verifyTarget(appAutoScalingClient, tableId, ns, tableWCUs),
            "Verification of scalable target failed.");
        assertDoesNotThrow(() -> EnableDynamoDBAutoscaling.configureScalingPolicy(appAutoScalingClient, tableId, ns, tableWCUs, policyName),
            "Failed to configure scaling policy.");
        System.out.println("\n EnableDynamoDBAutoscaling test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testDisableDynamoDBAutoscaling() {
        assertDoesNotThrow(() -> DisableDynamoDBAutoscaling.deletePolicy(appAutoScalingClient, policyName, tableWCUs, ns, tableId),
            "Failed to delete scaling policy.");
        assertDoesNotThrow(() -> DisableDynamoDBAutoscaling.verifyScalingPolicies(appAutoScalingClient, tableId, ns, tableWCUs),
            "Verification of scaling policies failed.");
        assertDoesNotThrow(() -> DisableDynamoDBAutoscaling.deregisterScalableTarget(appAutoScalingClient, tableId, ns, tableWCUs),
            "Failed to deregister scalable target.");
        assertDoesNotThrow(() -> DisableDynamoDBAutoscaling.verifyTarget(appAutoScalingClient, tableId, ns, tableWCUs),
            "Verification of scalable target after deregistration failed.");
        System.out.println("\n DisableDynamoDBAutoscaling test passed");
    }
}
