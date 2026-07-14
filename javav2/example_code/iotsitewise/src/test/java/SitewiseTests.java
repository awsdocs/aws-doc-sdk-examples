// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import com.example.iotsitewise.HelloSitewise;
import com.example.iotsitewise.scenario.CloudFormationHelper;
import com.example.iotsitewise.scenario.SitewiseActions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetModelResponse;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetResponse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SitewiseTests {
    private static final Logger logger = LoggerFactory.getLogger(SitewiseTests.class);
    private static final String assetModelName = "MyAssetModel" + UUID.randomUUID();
    private static final String assetName = "MyAsset";

    private static String assetId = "";
    private static final String gatewayName = "myGateway" + UUID.randomUUID();
    private static final String myThing = "myThing" + UUID.randomUUID();

    private static String assetModelId = "";

    private static final SitewiseActions sitewiseActions = new SitewiseActions();

    private static Map<String, String> propertyIds;

    private static String humPropId = "";

    private static String tempPropId = "";

    private static String gatewayId = "";
    private static final String ROLES_STACK = "RoleSitewise";

    @BeforeAll
    public static void setUp() {
        CloudFormationHelper.deployCloudFormationStack(ROLES_STACK);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testHelloService() {
        assertDoesNotThrow(HelloSitewise::fetchAssetModels);
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateAssetModel() {
        assertDoesNotThrow(() -> {
            CompletableFuture<CreateAssetModelResponse> future = sitewiseActions.createAssetModelAsync(assetModelName);
            CreateAssetModelResponse response = future.join();

            if (response == null || response.assetModelId() == null) {
                throw new RuntimeException("Simulating failure: response or assetModelId is null");
            }
            assetModelId = response.assetModelId();
            assertNotNull(assetModelId);
        });
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateAsset() throws InterruptedException {
        Thread.sleep(30000);
        assertDoesNotThrow(() -> {
            CompletableFuture<CreateAssetResponse> future = sitewiseActions.createAssetAsync(assetName, assetModelId);
            CreateAssetResponse response = future.join();
            assetId = response.assetId();
            assertNotNull(assetId);
        });
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetPropIds() {
        assertDoesNotThrow(() -> {
            propertyIds = sitewiseActions.getPropertyIds(assetModelId).join();
            humPropId = propertyIds.get("Humidity");
            logger.info("The Humidity property Id is " + humPropId);
            tempPropId = propertyIds.get("Temperature");
            logger.info("The Temperature property Id is " + tempPropId);
        });
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testSendProps() {
        assertDoesNotThrow(() -> {
            sitewiseActions.sendDataToSiteWiseAsync(assetId, tempPropId, humPropId).join();
        });
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testGETHumValue() {
        assertDoesNotThrow(() -> {
            sitewiseActions.getAssetPropValueAsync(humPropId, assetId);
        });
        logger.info("Test 6 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testCreateGateway() {
        assertDoesNotThrow(() -> {
            gatewayId = sitewiseActions.createGatewayAsync(gatewayName, myThing).join();
            assertNotNull(gatewayId);
        });
        logger.info("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testDescribeGateway() {
        assertDoesNotThrow(() -> {
            sitewiseActions.describeGatewayAsync(gatewayId).join();
        });
        logger.info("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testDeleteAsset() throws InterruptedException {
        Thread.sleep(30000);
        assertDoesNotThrow(() -> {
            sitewiseActions.deleteAssetAsync(assetId).join();
        });
        logger.info("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testDeleteAssetModel() throws InterruptedException {
        Thread.sleep(30000);
        assertDoesNotThrow(() -> {
            sitewiseActions.deleteAssetModelAsync(assetModelId).join();
        });
        CloudFormationHelper.destroyCloudFormationStack(ROLES_STACK);
        logger.info("Test 13 passed");
    }
}

