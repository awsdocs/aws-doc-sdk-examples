// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.firehose.addStockTradeData
import com.kotlin.firehose.createStream
import com.kotlin.firehose.delStream
import com.kotlin.firehose.listStreams
import com.kotlin.firehose.putSingleRecord
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
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class FirehoseTest {
    private val logger: Logger = LoggerFactory.getLogger(FirehoseTest::class.java)
    private var bucketARN = ""
    private var roleARN = ""
    private var newStream = ""
    private var textValue = ""
    private var delStream = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            bucketARN = values.bucketARN.toString()
            roleARN = values.roleARN.toString()
            newStream = values.newStream.toString() + UUID.randomUUID()
            textValue = values.textValue.toString()
        }

    @Test
    @Order(1)
    fun createDeliveryStreamTest() =
        runBlocking {
            createStream(bucketARN, roleARN, newStream)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun putRecordsTest() =
        runBlocking {
            // Wait for the resource to become available
            println("Wait 15 mins for resource to become available.")
            TimeUnit.MINUTES.sleep(15)
            putSingleRecord(textValue, newStream)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun putBatchRecordsTest() =
        runBlocking {
            addStockTradeData(newStream)
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun listDeliveryStreamsTest() =
        runBlocking {
            listStreams()
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun deleteStreamTest() =
        runBlocking {
            delStream(newStream)
            logger.info("Test 5 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/firehose"
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
    @DisplayName("A class used to get test values from test/firehose (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val bucketARN: String? = null
        val roleARN: String? = null
        val newStream: String? = null
        val textValue: String? = null
    }
}
