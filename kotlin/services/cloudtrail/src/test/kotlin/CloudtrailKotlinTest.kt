// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.cloudtrail.createNewTrail
import com.kotlin.cloudtrail.deleteSpecificTrail
import com.kotlin.cloudtrail.describeSpecificTrails
import com.kotlin.cloudtrail.getSelectors
import com.kotlin.cloudtrail.lookupAllEvents
import com.kotlin.cloudtrail.setSelector
import com.kotlin.cloudtrail.startLog
import com.kotlin.cloudtrail.stopLog
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class CloudtrailKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(CloudtrailKotlinTest::class.java)
    private var trailName = ""
    private var s3BucketName = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            val gson = Gson()
            val json: String = getSecretValues()
            val values: SecretValues = gson.fromJson<SecretValues>(json, SecretValues::class.java)
            trailName = values.trailName.toString()
            s3BucketName = values.s3BucketName.toString()
        }

    @Test
    @Order(1)
    fun createTrail() =
        runBlocking {
            createNewTrail(trailName, s3BucketName)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun putEventSelectors() =
        runBlocking {
            setSelector(trailName)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun getEventSelectors() =
        runBlocking {
            getSelectors(trailName)
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun lookupEvents() =
        runBlocking {
            lookupAllEvents()
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun describeTrails() =
        runBlocking {
            describeSpecificTrails(trailName)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun startLogging() =
        runBlocking {
            startLog(trailName)
            stopLog(trailName)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun deleteTrail() =
        runBlocking {
            deleteSpecificTrail(trailName)
            logger.info("Test 7 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/cloudtrail"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        SecretsManagerClient {
            region = "us-east-1"
        }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/cloudtrail (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val trailName: String? = null
        val s3BucketName: String? = null
    }
}
