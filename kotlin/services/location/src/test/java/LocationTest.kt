// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.location.listGeofences
import com.example.location.scenario.calcDistance
import com.example.location.scenario.createGeofenceCollection
import com.example.location.scenario.createKey
import com.example.location.scenario.createMap
import com.example.location.scenario.createRouteCalculator
import com.example.location.scenario.createTracker
import com.example.location.scenario.deleteGeofenceCollection
import com.example.location.scenario.deleteKey
import com.example.location.scenario.deleteMap
import com.example.location.scenario.deleteRouteCalculator
import com.example.location.scenario.deleteTracker
import com.example.location.scenario.getDevicePosition
import com.example.location.scenario.putGeofence
import com.example.location.scenario.reverseGeocode
import com.example.location.scenario.searchNearby
import com.example.location.scenario.searchText
import com.example.location.scenario.updateDevicePosition
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class LocationTest {
    private val logger: Logger = LoggerFactory.getLogger(LocationTest::class.java)
    private val mapName = "TestMap"
    private val keyName = "TestKey"
    private val collectionName = "TestCollection"
    private val existingCollectione = "Collection100"
    private val geoId = "TestGeo"
    private val trackerName = "TestTracker"
    private var mapArn = ""
    private var keyArn = ""
    var calculatorName = "TestCalc"
    var deviceId = "iPhone-112359" // Use the iPhone's identifier from Swift

    @Test
    @Order(1)
    fun testScenario() = runBlocking {
        println("===== Starting Full Location Service Scenario Test =====")
        try {
            // Step 1: Create Map
            mapArn = runCatching { createMap(mapName) }
                .onSuccess { Assertions.assertNotNull(it, "Expected map ARN to be non-null") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            logger.info("Created Map: $mapArn")

            // Step 2: Create Key
            keyArn = runCatching { createKey(keyName, mapArn) }
                .onSuccess { Assertions.assertNotNull(it, "Expected key ARN to be non-null") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            logger.info("Created Key: $keyArn")

            // Step 3: Geofencing
            runCatching { createGeofenceCollection(collectionName) }
                .onSuccess { println("Created Geofence Collection: $collectionName") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            runCatching { putGeofence(collectionName, geoId) }
                .onSuccess { println("Added Geofence: $geoId") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            // Step 4: Tracking
            runCatching { createTracker(trackerName) }
                .onSuccess { println(" Created Tracker: $trackerName") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            runCatching { updateDevicePosition(trackerName, deviceId) }
                .onSuccess { println("Updated Device Position: $deviceId") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            runCatching { getDevicePosition(trackerName, deviceId) }
                .onSuccess { println("Retrieved Device Position for: $deviceId") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            // Step 5: Route Calculation
            runCatching { createRouteCalculator(calculatorName) }
                .onSuccess { println("Created Route Calculator: $calculatorName") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            runCatching { calcDistance(calculatorName) }
                .onSuccess { println("Calculated Distance") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            // Step 6: Search Operations
            runCatching { reverseGeocode() }
                .onSuccess { println("Reverse Geocode Successful") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            runCatching { searchText("coffee shop") }
                .onSuccess { println("Search for 'coffee shop' Successful") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()

            runCatching { searchNearby() }
                .onSuccess { println(" Nearby Search Successful") }
                .onFailure { it.printStackTrace() }
                .getOrThrow()
        } finally {
            // Cleanup
            println("===== Starting Cleanup =====")
            val cleanupResults = listOf(
                runCatching { deleteMap(mapName) }.onFailure { it.printStackTrace() },
                runCatching { deleteKey(keyName) }.onFailure { it.printStackTrace() },
                runCatching { deleteGeofenceCollection(collectionName) }.onFailure { it.printStackTrace() },
                runCatching { deleteTracker(trackerName) }.onFailure { it.printStackTrace() },
                runCatching { deleteRouteCalculator(calculatorName) }.onFailure { it.printStackTrace() }
            )

            // Ensure cleanup didn't fail completely
            val cleanupSuccess = cleanupResults.all { it.isSuccess }
            Assertions.assertTrue(cleanupSuccess, "Some resources failed to delete")

            logger.info("===== Cleanup Completed Successfully =====")
        }

        logger.info("🎉 Test 1 Passed Successfully!")
    }

    @Test
    @Order(2)
    fun testHello() = runBlocking {
        runCatching { listGeofences(existingCollectione) }
            .onSuccess { println("Hello passed") }
            .onFailure { it.printStackTrace() }
            .getOrThrow()

        logger.info("🎉 Test 2 Passed Successfully!")
    }
}