// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class PinpointKotlinTest {
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
    fun setup() = runBlocking{
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

        /*
        try {
            val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
            val prop = Properties()

            // load the properties file.
            prop.load(input)
            appName = prop.getProperty("appName")
            originationNumber = prop.getProperty("originationNumber")
            destinationNumber = prop.getProperty("destinationNumber")
            message = prop.getProperty("message")
            userId = prop.getProperty("userId")
            senderAddress = prop.getProperty("senderAddress")
            toAddress = prop.getProperty("toAddress")
            subject = prop.getProperty("subject")
            existingApp = prop.getProperty("existingApp")
            existingEndpoint = prop.getProperty("existingEndpoint")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
         */
    }

    @Test
    @Order(1)
    fun createAppTest() = runBlocking {
        appId = createApplication(appName).toString()
        assertTrue(!appId.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun createEndpointTest() = runBlocking {
        endpointId = createPinpointEndpoint(appId).toString()
        assertTrue(!endpointId.isEmpty())
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun addExampleEndpointTest() = runBlocking {
        updateEndpointsViaBatch(appId)
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun lookUpEndpointTest() = runBlocking {
        lookupPinpointEndpoint(appId, endpointId)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun deleteEndpointTest() = runBlocking {
        deletePinEncpoint(appId, endpointId)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun sendMessageTest() = runBlocking {
        sendSMSMessage(message, appId, originationNumber, destinationNumber)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun createSegmentTest() = runBlocking {
        segmentId = createPinpointSegment(appId).toString()
        assertTrue(!segmentId.isEmpty())
        println("Test 7 passed")
    }

    @Test
    @Order(8)
    fun listSegmentsTest() = runBlocking {
        listSegs(appId)
        println("Test 8 passed")
    }

    @Test
    @Order(9)
    fun createCampaignTest() = runBlocking {
        createPinCampaign(appId, segmentId)
        println("Test 9 passed")
    }

    @Test
    @Order(10)
    fun sendEmailMessageTest() = runBlocking {
        sendEmail(subject, senderAddress, toAddress)
        println("Test 10 passed")
    }

    @Test
    @Order(11)
    fun listEndpointIdsTest() = runBlocking {
        listAllEndpoints(existingApp, userId)
        println("Test 11 passed")
    }

    @Test
    @Order(12)
    fun deleteAppTest() = runBlocking {
        deletePinApp(appId)
        println("Test 12 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/pinpoint"
        val valueRequest= GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
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
