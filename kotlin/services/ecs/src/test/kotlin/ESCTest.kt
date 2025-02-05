// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.ecs.createGivenCluster
import com.kotlin.ecs.createNewService
import com.kotlin.ecs.deleteSpecificService
import com.kotlin.ecs.descCluster
import com.kotlin.ecs.getAllTasks
import com.kotlin.ecs.listAllClusters
import com.kotlin.ecs.updateSpecificService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class ESCTest {
    private val logger: Logger = LoggerFactory.getLogger(ESCTest::class.java)
    var clusterName = ""
    var clusterARN = ""
    var securityGroups: String = ""
    var taskId: String = ""
    var subnet: String = ""
    var serviceName: String = ""
    var serviceArn: String = ""
    var taskDefinition: String = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            clusterName = values.clusterName.toString() + UUID.randomUUID()
            taskId = values.taskId.toString()
            subnet = values.subnet.toString()
            securityGroups = values.securityGroups.toString()
            serviceName = values.serviceName.toString() + UUID.randomUUID()
            taskDefinition = values.taskDefinition.toString()
        }

    @Test
    @Order(1)
    fun createClusterTest() =
        runBlocking {
            clusterARN = createGivenCluster(clusterName).toString()
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createServiceTest() =
        runBlocking {
            serviceArn = createNewService(clusterARN, serviceName, securityGroups, subnet, taskDefinition).toString()
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun listClustersTest() =
        runBlocking {
            listAllClusters()
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun describeClustersTest() =
        runBlocking {
            descCluster(clusterARN)
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun listTaskDefinitionsTest() =
        runBlocking {
            getAllTasks(clusterARN, taskId)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun updateServiceTest() =
        runBlocking {
            updateSpecificService(clusterARN, serviceArn)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun deleteServiceTest() =
        runBlocking {
            deleteSpecificService(clusterARN, serviceArn)
            logger.info("Test 7 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretClient =
            SecretsManagerClient {
                region = "us-east-1"
            }
        val secretName = "test/ecs"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        val valueResponse = secretClient.getSecretValue(valueRequest)
        return valueResponse.secretString.toString()
    }

    @Nested
    @DisplayName("A class used to get test values from test/ecs (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val clusterName: String? = null
        val securityGroups: String? = null
        val subnet: String? = null
        val taskId: String? = null
        val serviceName: String? = null
        val taskDefinition: String? = null
    }
}
