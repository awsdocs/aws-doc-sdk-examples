// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.neptune.NeptuneClient
import com.example.neptune.scenerio.checkInstanceStatus
import com.example.neptune.scenerio.createDbCluster
import com.example.neptune.scenerio.createDbInstance
import com.example.neptune.scenerio.createSubnetGroup
import com.example.neptune.scenerio.deleteDBCluster
import com.example.neptune.scenerio.deleteDBSubnetGroup
import com.example.neptune.scenerio.deleteDbInstance
import com.example.neptune.scenerio.describeDBClusters
import com.example.neptune.scenerio.startDBCluster
import com.example.neptune.scenerio.stopDBCluster
import com.example.neptune.scenerio.waitForClusterStatus
import com.example.neptune.scenerio.waitUntilInstanceDeleted
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class NeptuneTest {
    private val subnetGroupName = "neptuneSubnetGroup65"
    private val clusterName = "neptuneCluster65"
    private val dbInstanceId = "neptuneDB65"
    private var dbClusterId = ""
    private lateinit var client: NeptuneClient

    @BeforeAll
    fun setup() = runBlocking {
        client = NeptuneClient.fromEnvironment {region = "us-east-1" }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    fun testSubnetGroup() = runBlocking {
        runCatching {
            createSubnetGroup(client, subnetGroupName)
        }.onFailure {
            it.printStackTrace()
            Assertions.fail("Subnet group creation failed: ${it.message}")
        }.getOrThrow()

        println("Created Group: $subnetGroupName")
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    fun testCreateDbCluster() = runBlocking {
        dbClusterId = runCatching {
            createDbCluster(client, clusterName)
        }.onFailure {
            it.printStackTrace()
            Assertions.fail("DB cluster creation failed: ${it.message}")
        }.getOrThrow()

        Assertions.assertNotNull(dbClusterId, "Expected DB cluster ID to be non-null")
        println("Created DB Cluster: $dbClusterId")
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    fun testCreateDbInstance() = runBlocking {
        runCatching {
            createDbInstance(client, dbInstanceId, dbClusterId)
        }.onFailure {
            it.printStackTrace()
            Assertions.fail("DB Instance creation failed: ${it.message}")
        }.getOrThrow()
        println("Created DB Instance: $dbInstanceId")
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    fun testCheckInstanceStatus() = runBlocking {
        runCatching {
            checkInstanceStatus(client, dbInstanceId, "available")
        }.onFailure {
            it.printStackTrace()
            Assertions.fail("Instance status check failed: ${it.message}")
        }.getOrThrow()
        println("Instance status check passed: $dbInstanceId")
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    fun testDescribeDBClusters() = runBlocking {
        runCatching {
            describeDBClusters(client, dbClusterId)
        }.onFailure {
            it.printStackTrace()
            Assertions.fail("Describe Cluster failed: ${it.message}")
        }.getOrThrow()
        println("Describe cluster passed: $dbInstanceId")
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    fun testStopClusters() = runBlocking {
        runCatching {
            stopDBCluster(client, dbClusterId)
            waitForClusterStatus(client, dbClusterId, "stopped")
        }.onFailure {
            it.printStackTrace()
            Assertions.fail("Stopping the Cluster failed: ${it.message}")
        }.getOrThrow()
        println("Stopping the cluster passed: $dbInstanceId")
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    fun testStartClusters() = runBlocking {
        runCatching {
            startDBCluster(client, dbClusterId)
            waitForClusterStatus(client, dbClusterId, "available")
            checkInstanceStatus(client, dbInstanceId, "available")
        }.onFailure {
            it.printStackTrace()
            Assertions.fail("Starting the Cluster failed: ${it.message}")
        }.getOrThrow()
        println("Starting the cluster passed: $dbInstanceId")
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    fun testDeleteResources() = runBlocking {
        runCatching {
            deleteDbInstance(client, dbInstanceId)
            waitUntilInstanceDeleted(client, dbInstanceId)
            deleteDBCluster(client, dbClusterId)
            deleteDBSubnetGroup(client, subnetGroupName)
        }.onFailure {
            it.printStackTrace()
            Assertions.fail("Deleting the resources failed: ${it.message}")
        }.getOrThrow()
        println("Deleting the resources passed: $dbInstanceId")
    }
}
