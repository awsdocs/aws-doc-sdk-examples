// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.fleetwise.listSignalCatalogs
import com.example.fleetwise.scenario.createDecoderManifest
import com.example.fleetwise.scenario.createFleet
import com.example.fleetwise.scenario.createModelManifest
import com.example.fleetwise.scenario.createThingIfNotExist
import com.example.fleetwise.scenario.createVehicle
import com.example.fleetwise.scenario.createbranchVehicle
import com.example.fleetwise.scenario.deleteDecoderManifest
import com.example.fleetwise.scenario.deleteFleet
import com.example.fleetwise.scenario.deleteModelManifest
import com.example.fleetwise.scenario.deleteSignalCatalog
import com.example.fleetwise.scenario.deleteVehicle
import com.example.fleetwise.scenario.getVehicleDetails
import com.example.fleetwise.scenario.listSignalCatalogNode
import com.example.fleetwise.scenario.updateDecoderManifest
import com.example.fleetwise.scenario.updateModelManifest
import com.example.fleetwise.scenario.waitForDecoderManifestActive
import com.example.fleetwise.scenario.waitForModelManifestActive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FleetwiseTest {
    private val signalCatalogName = "catalogTest10"
    private val manifestName = "manifest10"
    private val fleetName = "fleet10"
    private val vecName = "vehicle10"
    private val decName = "decManifest10"
    private var signalCatalogArn = ""
    private var fleetValue = ""
    private var manifestArn = ""
    private var decArn = ""

    @Test
    @Order(1)
    fun testHello() = runBlocking {
        runCatching {
            listSignalCatalogs()
        }.onSuccess {
            println("Test 1 Passed Successfully!")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(2)
    fun testCat() = runBlocking {
        runCatching {
            signalCatalogArn = createbranchVehicle(signalCatalogName)
        }.onSuccess {
            println("Test 2 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(3)
    fun testFleet(): Unit = runBlocking {
        runCatching {
            fleetValue = createFleet(signalCatalogArn, fleetName)
        }.onSuccess {
            println("Test 3 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(4)
    fun testModelManifest(): Unit = runBlocking {
        runCatching {
            val nodes = listSignalCatalogNode(signalCatalogName)
            manifestArn = nodes?.let { createModelManifest(manifestName, signalCatalogArn, it) }.toString()
        }.onSuccess {
            println("Test 4 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(5)
    fun testDecodeManifest(): Unit = runBlocking {
        runCatching {
            decArn = createDecoderManifest(decName, manifestArn)
        }.onSuccess {
            println("Test 5 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(6)
    fun testManifestStatus(): Unit = runBlocking {
        runCatching {
            updateModelManifest(manifestName)
            waitForModelManifestActive(manifestName)
        }.onSuccess {
            println("Test 6 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(7)
    fun testDecoderStatus(): Unit = runBlocking {
        runCatching {
            updateDecoderManifest(decName)
            waitForDecoderManifestActive(decName)
        }.onSuccess {
            println("Test 7 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(8)
    fun testIoTThing(): Unit = runBlocking {
        runCatching {
            createThingIfNotExist(vecName)
        }.onSuccess {
            println("Test 8 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(9)
    fun testVehicle(): Unit = runBlocking {
        runCatching {
            createVehicle(vecName, manifestArn, decArn)
        }.onSuccess {
            println("Test 9 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(10)
    fun testGetVehicle(): Unit = runBlocking {
        runCatching {
            getVehicleDetails(vecName)
        }.onSuccess {
            println("Test 10 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(11)
    fun testDelVehicle() = runBlocking {
        runCatching {
            deleteVehicle(vecName)
        }.onSuccess {
            println("Test 11 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(12)
    fun testDelDecoder() = runBlocking {
        runCatching {
            deleteDecoderManifest(decName)
        }.onSuccess {
            println("Test 12 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(13)
    fun testDelMan() = runBlocking {
        runCatching {
            deleteModelManifest(manifestName)
        }.onSuccess {
            println("Test 13 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(14)
    fun testDelFleet() = runBlocking {
        runCatching {
            deleteFleet(fleetName)
        }.onSuccess {
            println("Test 14 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    @Order(15)
    fun testDelCat() = runBlocking {
        runCatching {
            deleteSignalCatalog(signalCatalogName)
        }.onSuccess {
            println("Test 15 Passed Successfully")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }
}
