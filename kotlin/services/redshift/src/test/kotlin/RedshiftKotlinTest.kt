// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.redshift.User
import com.kotlin.redshift.createCluster
import com.kotlin.redshift.deleteRedshiftCluster
import com.kotlin.redshift.describeRedshiftClusters
import com.kotlin.redshift.findReservedNodeOffer
import com.kotlin.redshift.listRedShiftEvents
import com.kotlin.redshift.modifyCluster
import com.kotlin.redshift.waitForClusterReady
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.util.Random

/**
 * To run these integration tests, you need to either set the required values
 * in the config.properties file or in AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class RedshiftKotlinTest {
    private var clusterId = ""
    private var eventSourceType = ""
    private var secretName = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            val rand = Random()
            val randomNum = rand.nextInt(10000 - 1 + 1) + 1

            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues().toString()
            val values = gson.fromJson(json, SecretValues::class.java)
            clusterId = values.clusterId + randomNum
            secretName = values.secretName.toString()
            eventSourceType = values.eventSourceType.toString()

// Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
/*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        clusterId = prop.getProperty("clusterId")
        eventSourceType = prop.getProperty("eventSourceType")
        secretName  prop.getProperty("secretName")
 */
        }

    @Test
    @Order(1)
    fun createClusterTest() =
        runBlocking {
            val gson = Gson()
            val user =
                gson.fromJson(
                    com.kotlin.redshift
                        .getSecretValues(secretName)
                        .toString(),
                    User::class.java
                )
            val username = user.username
            val userPassword = user.password
            createCluster(clusterId, username, userPassword)
            println("Test 2 passed")
        }

    @Test
    @Order(2)
    fun waitForClusterReadyTest() =
        runBlocking {
            waitForClusterReady(clusterId)
            println("Test 3 passed")
        }

    @Test
    @Order(3)
    fun modifyClusterReadyTest() =
        runBlocking {
            modifyCluster(clusterId)
            println("Test 4 passed")
        }

    @Test
    @Order(4)
    fun describeClustersTest() =
        runBlocking {
            describeRedshiftClusters()
            println("Test 5 passed")
        }

    @Test
    @Order(5)
    fun findReservedNodeOfferTest() =
        runBlocking {
            findReservedNodeOffer()
            println("Test 6 passed")
        }

    @Test
    @Order(6)
    fun listEventsTest() =
        runBlocking {
            listRedShiftEvents(clusterId, eventSourceType)
            println("Test 7 passed")
        }

    @Test
    @Order(7)
    fun deleteClusterTest() =
        runBlocking {
            deleteRedshiftCluster(clusterId)
            println("Test 8 passed")
        }

    suspend fun getSecretValues(): String? {
        val secretName = "test/red"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }

        SecretsManagerClient { region = "us-east-1" }.use { secretsClient ->
            val valueResponse = secretsClient.getSecretValue(valueRequest)
            return valueResponse.secretString
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/rds (an AWS Secrets Manager secret)")
    internal inner class SecretValues {
        val clusterId: String? = null
        val eventSourceType: String? = null
        val secretName: String? = null
    }
}
