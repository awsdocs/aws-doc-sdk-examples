// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.sqs.addTags
import com.kotlin.sqs.createQueue
import com.kotlin.sqs.deleteMessages
import com.kotlin.sqs.deleteQueue
import com.kotlin.sqs.listTags
import com.kotlin.sqs.receiveMessages
import com.kotlin.sqs.removeTag
import com.kotlin.sqs.sendBatchMessages
import com.kotlin.sqs.sendMessages
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class SQSTest {
    private var queueName = ""
    private var message = ""
    private var queueUrl = ""
    
    @BeforeAll
    fun setup() = runBlocking {
        val random = Random()
        val randomNum = random.nextInt(10000 - 1 + 1) + 1

        // Get the values to run these tests from AWS Secrets Manager.
        val gson = Gson()
        val json: String = getSecretValues()
        val queueMessage = gson.fromJson(json, QueueMessage::class.java)
        queueName = queueMessage.queueName.toString() + randomNum
        message = queueMessage.message.toString()

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // load the properties file.
        prop.load(input)
        queueName = prop.getProperty("QueueName")
        message = prop.getProperty("Message")
        */
    }

    @Test
    @Order(1)
    fun createSQSQueueTest() = runBlocking {
        queueUrl = createQueue(queueName)
        Assertions.assertTrue(!queueUrl.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun sendMessageTest() = runBlocking {
        sendMessages(queueUrl, message)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun sendBatchMessagesTest() = runBlocking {
        sendBatchMessages(queueUrl)
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun getMessageTest() = runBlocking {
        receiveMessages(queueUrl)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun addQueueTagsTest() = runBlocking {
        addTags(queueName)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun listQueueTagsTest() = runBlocking {
        listTags(queueName)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun removeQueueTagsTest() = runBlocking {
        removeTag(queueName, "Test")
        println("Test 7 passed")
    }

    @Test
    @Order(8)
    fun deleteMessagesTest() = runBlocking {
        deleteMessages(queueUrl)
        println("Test 8 passed")
    }

    @Test
    @Order(9)
    fun deleteQueueTest() = runBlocking {
        deleteQueue(queueUrl)
        println("Test 9 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/sqs"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/sns, an AWS Secrets Manager secret")
    internal class QueueMessage {
        val queueName: String? = null
        val dLQueueName: String? = null
        val message: String? = null
    }
}
