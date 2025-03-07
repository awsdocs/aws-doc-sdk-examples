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
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LocationTest {

    private static final LocationActions locationActions = new LocationActions();

    private static final String mapName = "TestMap";

    private static final String keyName = "TestKey";
    private static final String collectionName = "TestCollection";
    private static final String geoId = "TestGeo";
    private static final String trackerName = "TestTracker";

    private static String mapArn = "";
    String calculatorName = "TestCalc";
    String deviceId = "iPhone-111356"; // Use the iPhone's identifier from Swift

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateMap() {
        assertDoesNotThrow(() -> {
            mapArn = locationActions.createMap(mapName).join();
            assertNotNull(mapArn);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateKey() {
        assertDoesNotThrow(() -> {
            locationActions.createKey(keyName, mapArn).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateGeofenceCollection() {
        assertDoesNotThrow(() -> {
            locationActions.createGeofenceCollection(collectionName).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testPutGeoCollection() {
        assertDoesNotThrow(() -> {
            locationActions.putGeofence(collectionName, geoId).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testHelloService() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = HelloLocation.listGeofences(collectionName);
            future.join(); // Wait for the asynchronous operation to complete
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testCreateTracker() {
        assertDoesNotThrow(() -> {
            locationActions.createTracker(trackerName).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testUpdateDevice() {
        assertDoesNotThrow(() -> {
            locationActions.updateDevicePosition(trackerName, deviceId).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testGetDevicePosition() {
        assertDoesNotThrow(() -> {
            locationActions.getDevicePosition(trackerName, deviceId).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testCreateRouteCalculator() {
        assertDoesNotThrow(() -> {
            locationActions.createRouteCalculator(calculatorName).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testCreateRDistance() {
        assertDoesNotThrow(() -> {
            locationActions.calcDistanceAsync(calculatorName).join();
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
        });
    }

}
