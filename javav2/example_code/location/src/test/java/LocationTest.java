// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.location.HelloLocation;
import com.example.location.scenario.LocationActions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.location.model.BatchUpdateDevicePositionResponse;
import software.amazon.awssdk.services.location.model.CalculateRouteResponse;
import software.amazon.awssdk.services.location.model.CreateGeofenceCollectionResponse;
import software.amazon.awssdk.services.location.model.CreateRouteCalculatorResponse;
import software.amazon.awssdk.services.location.model.GetDevicePositionRequest;
import software.amazon.awssdk.services.location.model.GetDevicePositionResponse;
import software.amazon.awssdk.services.location.model.PutGeofenceResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LocationTest {

    private static final LocationActions locationActions = new LocationActions();

    private static final Logger logger = LoggerFactory.getLogger(LocationTest.class);
    private static final String mapName = "TestMap1";

    private static final String keyName = "TestKey1";
    private static final String collectionName = "TestCollection1";
    private static final String geoId = "TestGeo1";
    private static final String trackerName = "TestTracker1";

    private static String mapArn = "";
    String calculatorName = "TestCalc";
    String deviceId = "iPhone-112359"; // Use the iPhone's identifier from Swift

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateMap() {
        assertDoesNotThrow(() -> {
            mapArn = locationActions.createMap(mapName).join();
            assertNotNull(mapArn);
            logger.info("Test 1 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateKey() {
        CompletableFuture<String> future = locationActions.createKey(keyName, mapArn);
        assertDoesNotThrow(() -> {
            String keyArn = future.join();
            assertNotNull(keyArn, "Expected key ARN to be non-null");
            assertFalse(keyArn.isEmpty(), "Expected key ARN to be non-empty");
            logger.info("Test 2 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateGeofenceCollection() {
        CompletableFuture<String> future = locationActions.createGeofenceCollection(collectionName);
        assertDoesNotThrow(() -> {
            String response = future.join();
            assertNotNull(response, "Expected response to be non-null");
            logger.info("Test 3 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testPutGeoCollection() {
        CompletableFuture<PutGeofenceResponse> future = locationActions.putGeofence(collectionName, geoId);
        assertDoesNotThrow(() -> {
            PutGeofenceResponse response = future.join();
            assertNotNull(response, "Expected response to be non-null");
            logger.info("Test 4 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testHelloService() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = HelloLocation.listGeofences(collectionName);
            future.join(); // Wait for the asynchronous operation to complete
            logger.info("Test 5 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testCreateTracker() {
        CompletableFuture<String> future = locationActions.createTracker(trackerName);
        assertDoesNotThrow(() -> {
            String trackerArn = future.join();
            assertNotNull(trackerArn, "Expected tracker ARN to be non-null");
            assertFalse(trackerArn.isEmpty(), "Expected tracker ARN to be non-empty");
            logger.info("Test 6 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testUpdateDevice() {
        CompletableFuture<BatchUpdateDevicePositionResponse> future = locationActions.updateDevicePosition(trackerName, deviceId);
        assertDoesNotThrow(() -> {
            BatchUpdateDevicePositionResponse response = future.join();
            assertNotNull(response, "Expected response to be non-null");
            assertTrue(response.errors().isEmpty(), "Expected no errors while updating device position");
            logger.info("Test 7 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testGetDevicePosition() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        CompletableFuture<GetDevicePositionResponse> future = locationActions.getDevicePosition(trackerName, deviceId);
        assertDoesNotThrow(() -> {
            GetDevicePositionResponse response = future.join();
            assertNotNull(response, "Expected response to be non-null");
          //  assertNotNull(response.position(), "Expected position data to be non-null");
         //   assertFalse(response.position().isEmpty(), "Expected position data to be non-empty");
          //  assertNotNull(response.receivedTime(), "Expected received time to be non-null");
            logger.info("Test 8 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testCreateRouteCalculator() {
        CompletableFuture<CreateRouteCalculatorResponse> future = locationActions.createRouteCalculator(calculatorName);
        assertDoesNotThrow(() -> {
            CreateRouteCalculatorResponse response = future.join();
            assertNotNull(response, "Expected response to be non-null");
            assertNotNull(response.calculatorArn(), "Expected calculator ARN to be non-null");
            assertFalse(response.calculatorArn().isEmpty(), "Expected calculator ARN to be non-empty");
            logger.info("Test 9 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testCalcDistance() {
        CompletableFuture<CalculateRouteResponse> future = locationActions.calcDistanceAsync(calculatorName);
        assertDoesNotThrow(() -> {
            CalculateRouteResponse response = future.join();
            assertNotNull(response);
            assertNotNull(response.summary());
            double distance = response.summary().distance();
            double duration = response.summary().durationSeconds();
            assertTrue(distance > 0, "Expected distance to be greater than 0");
            assertTrue(duration > 0, "Expected duration to be greater than 0");
            logger.info("Test 10 passed");
        });
    }

    @Tag("IntegrationTest")
    @Order(11)
    public void testGeoPlaces() {

        assertDoesNotThrow(() -> {
            locationActions.reverseGeocode();
            locationActions.searchText("coffee shop");
            locationActions.searchNearBy();
            logger.info("Test 11 passed");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testDeleteLocationResources() {
        assertDoesNotThrow(() -> {
            locationActions.deleteMap(mapName).join();
            locationActions.deleteKey(keyName).join();
            locationActions.deleteGeofenceCollectionAsync(collectionName).join();
            locationActions.deleteTracker(trackerName).join();
            locationActions.deleteRouteCalculator(calculatorName).join();
            logger.info("Test 12 passed");
        });
    }
}
