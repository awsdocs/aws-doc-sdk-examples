// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.iot.HelloIoT;
import com.example.iot.IotScenario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IoTTests {

    private static IotClient iotClient;
    private static String thingName = "" ;

    private static String roleARN = "" ;

    private static String ruleName = "" ;

    private static String snsAction = "" ;

    private static String queryString = "" ;

    @BeforeAll
    public static void setUp() throws IOException {
        iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try (InputStream input = IoTTests.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            thingName = prop.getProperty("thingName");
            roleARN = prop.getProperty("roleARN");
            ruleName = prop.getProperty("ruleName");
            snsAction = prop.getProperty("snsAction");
            queryString = "thingName:"+thingName+"";


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testHello() {
        assertDoesNotThrow(() -> HelloIoT.listAllThings(iotClient),
            "Failed to list your things.");
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testIotScenario() throws InterruptedException {
        assertDoesNotThrow(() -> IotScenario.createIoTThing(iotClient, thingName),
            "Failed to create your thing in the scenario.");

        String certArn = assertDoesNotThrow(() -> IotScenario.createCertificate(iotClient),
            "Failed to create a cert in the scenario.");

        assertDoesNotThrow(() -> IotScenario.attachCertificateToThing(iotClient, thingName, certArn),
            "Failed to attach a cert in the scenario.");

        assertDoesNotThrow(() -> IotScenario.updateThing(iotClient, thingName),
            "Failed to update the thing in the scenario.");

        String endpointUrl = assertDoesNotThrow(() -> IotScenario. describeEndpoint(iotClient),
            "Failed to update the thing in the scenario.");

        IotDataPlaneClient iotPlaneClient = IotDataPlaneClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create(endpointUrl))
            .build();

        assertDoesNotThrow(() -> IotScenario.listCertificates(iotClient),
            "Failed to update the thing in the scenario.");

        assertDoesNotThrow(() -> IotScenario.detachThingPrincipal(iotClient, thingName, certArn),
            "Failed to detach cert in the scenario.");

        assertDoesNotThrow(() -> IotScenario.updateShawdowThing(iotPlaneClient, thingName),
            "Failed to update shawdow in the scenario.");

        assertDoesNotThrow(() -> IotScenario.getPayload(iotPlaneClient, thingName),
            "Failed to get payload in the scenario.");

        assertDoesNotThrow(() -> IotScenario.createIoTRule(iotClient, roleARN, ruleName, snsAction),
            "Failed to get payload in the scenario.");

        assertDoesNotThrow(() -> IotScenario.listIoTRules(iotClient),
            "Failed to list rules in the scenario.");

        System.out.println("Wait 10 secs");
        Thread.sleep(10000);

        assertDoesNotThrow(() ->IotScenario.searchThings(iotClient, queryString),
            "Failed to search things in the scenario.");

        assertDoesNotThrow(() -> IotScenario.deleteIoTThing(iotClient, thingName),
            "Failed to delete your thing in the scenario.");

        System.out.println("Scenario test passed");
    }
}
