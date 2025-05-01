// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.fleetwise.HelloFleetwise;
import com.example.fleetwise.scenario.FleetwiseActions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.iotfleetwise.model.Node;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FleetwiseTest {
    private static final Logger logger = LoggerFactory.getLogger(FleetwiseTest.class);
    private static String signalCatalogName = "catalogtest";
    private static String manifestName = "manifesttest";
    private static String fleetId = "fleettest";
    private static String vecName = "vehicletest";
    private static String decName = "decManifesttest";
    private static String signalCatalogArn = "" ;
    private static String fleetid = "" ;
    private static String manifestArn = "";
    private static String decArn = "";
    static FleetwiseActions actions = new FleetwiseActions();

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testHelloService() {
        assertDoesNotThrow(HelloFleetwise::ListSignalCatalogs);
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateCollection() {
        assertDoesNotThrow(() -> {
            signalCatalogArn = actions.createSignalCatalogAsync(signalCatalogName).join();
            assertTrue(signalCatalogArn.startsWith("arn:"), "The ARN should start with 'arn:'");
        });
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateFleet() {
        assertDoesNotThrow(() -> {
            fleetid = actions.createFleetAsync(signalCatalogArn, fleetId).join();;
            assertNotNull(fleetid, "The returned fleet ID should not be null");
        });
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testCreateManifest() {
        assertDoesNotThrow(() -> {
            List<Node> nodes = actions.listSignalCatalogNodeAsync(signalCatalogName).join();
            assertNotNull(nodes, "The returned node list should not be null");
            manifestArn = actions.createModelManifestAsync(manifestName,signalCatalogArn,nodes).join();;
            assertNotNull(manifestArn, "The returned manifest Arn should not be null");
        });
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testCreateDecoder() {
        assertDoesNotThrow(() -> {
            decArn = actions.createDecoderManifestAsync(decName ,manifestArn).join();
            assertNotNull(decArn, "The returned decoder should not be null");
        });
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testModelStatus() {
        assertDoesNotThrow(() -> {
            actions.updateModelManifestAsync(manifestName);
            actions.waitForModelManifestActiveAsync(manifestName).join();
        });
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testMDecoderStatus() {
        assertDoesNotThrow(() -> {
            actions.updateDecoderManifestAsync(decName);
            actions.waitForDecoderManifestActiveAsync(decName).join() ;
        });
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testCreateThing() {
        assertDoesNotThrow(() -> {
            actions.createThingIfNotExistsAsync(vecName).join();
        });
        logger.info("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testCreateVehicle() {
        assertDoesNotThrow(() -> {
            actions.createVehicleAsync(vecName, manifestArn,decArn).join();
        });
        logger.info("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testGetVehicle() {
        assertDoesNotThrow(() -> {
            actions.getVehicleDetailsAsync(vecName).join();
        });
        logger.info("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testGDeleteResources() {
        assertDoesNotThrow(() -> {
            actions.deleteVehicleAsync(vecName).join();
            actions.deleteDecoderManifestAsync(decName).join();
            actions.deleteModelManifestAsync(manifestName).join();
            actions.deleteFleetAsync(fleetid).join();
            actions.deleteSignalCatalogAsync(signalCatalogName).join();
        });
        logger.info("Test 11 passed");
    }
}
