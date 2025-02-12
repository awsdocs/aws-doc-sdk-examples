// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.pinpoint.createApplication
import com.kotlin.pinpoint.createPinCampaign
import com.kotlin.pinpoint.createPinpointEndpoint
import com.kotlin.pinpoint.createPinpointSegment
import com.kotlin.pinpoint.deletePinApp
import com.kotlin.pinpoint.deletePinEncpoint
import com.kotlin.pinpoint.listAllEndpoints
import com.kotlin.pinpoint.listSegs
import com.kotlin.pinpoint.lookupPinpointEndpoint
import com.kotlin.pinpoint.sendEmail
import com.kotlin.pinpoint.sendSMSMessage
import com.kotlin.pinpoint.updateEndpointsViaBatch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
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
class PinpointKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(PinpointKotlinTest::class.java)
    private var appName = ""
    private var appId = ""
    private var endpointId = ""
    private var segmentId = ""
    private var userId = ""
    private var subject = ""
    private var senderAddress = ""
    private var toAddress = ""
    private var originationNumber = ""
    private var destinationNumber = ""
    private var message = ""
    private var existingApp = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val valuesOb = gson.fromJson(json, SecretValues::class.java)
            appName = valuesOb.appName.toString()
            originationNumber = valuesOb.originationNumber.toString()
            destinationNumber = valuesOb.destinationNumber.toString()
            message = valuesOb.message.toString()
            userId = valuesOb.userId.toString()
            senderAddress = valuesOb.senderAddress.toString()
            toAddress = valuesOb.toAddress.toString()
            subject = valuesOb.subject.toString()
            existingApp = valuesOb.existingApplicationId.toString()
        }

    @Test
    @Order(1)
    fun createAppTest() =
        runBlocking {
            appId = createApplication(appName).toString()
            assertTrue(!appId.isEmpty())
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createEndpointTest() =
        runBlocking {
            endpointId = createPinpointEndpoint(appId).toString()
            assertTrue(!endpointId.isEmpty())
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun addExampleEndpointTest() =
        runBlocking {
            updateEndpointsViaBatch(appId)
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun lookUpEndpointTest() =
        runBlocking {
            lookupPinpointEndpoint(appId, endpointId)
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun deleteEndpointTest() =
        runBlocking {
            deletePinEncpoint(appId, endpointId)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun sendMessageTest() =
        runBlocking {
            sendSMSMessage(message, appId, originationNumber, destinationNumber)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun createSegmentTest() =
        runBlocking {
            segmentId = createPinpointSegment(appId).toString()
            assertTrue(!segmentId.isEmpty())
            logger.info("Test 7 passed")
        }

    @Test
    @Order(8)
    fun listSegmentsTest() =
        runBlocking {
            listSegs(appId)
            logger.info("Test 8 passed")
        }

    @Test
    @Order(9)
    fun createCampaignTest() =
        runBlocking {
            createPinCampaign(appId, segmentId)
            logger.info("Test 9 passed")
        }

    @Test
    @Order(10)
    fun sendEmailMessageTest() =
        runBlocking {
            sendEmail(subject, senderAddress, toAddress)
            logger.info("Test 10 passed")
        }

    @Test
    @Order(11)
    fun listEndpointIdsTest() =
        runBlocking {
            listAllEndpoints(existingApp, userId)
            logger.info("Test 11 passed")
        }

    @Test
    @Order(12)
    fun deleteAppTest() =
        runBlocking {
            deletePinApp(appId)
            logger.info("Test 12 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/pinpoint"
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
    @DisplayName("A class used to get test values from test/pinpoint (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val appName: String? = null
        val existingApplicationId: String? = null
        val userId: String? = null
        val subject: String? = null
        val senderAddress: String? = null
        val toAddress: String? = null
        val originationNumber: String? = null
        val destinationNumber: String? = null
        val message: String? = null
    }
}
