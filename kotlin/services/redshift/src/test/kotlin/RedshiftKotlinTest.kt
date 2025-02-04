// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.redshift.createCluster
import com.kotlin.redshift.describeRedshiftClusters
import com.kotlin.redshift.findReservedNodeOffer
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
import java.util.Random

/**
 * To run these integration tests, you need to either set the required values
 * in the config.properties file or in AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class RedshiftKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(RedshiftKotlinTest::class.java)
    private var clusterId = ""
    private var eventSourceType = ""
    private var username = ""
    private var password = ""

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
            username = values.userName.toString()
            password = values.password.toString()
            eventSourceType = values.eventSourceType.toString()
        }

    @Test
    @Order(1)
    fun createClusterTest() =
        runBlocking {
            createCluster(clusterId, username, password)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun describeClustersTest() =
        runBlocking {
            describeRedshiftClusters()
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun findReservedNodeOfferTest() =
        runBlocking {
            findReservedNodeOffer()
            logger.info("Test 3 passed")
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
        val userName: String? = null
        val password: String? = null
    }
}
