// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.lex.createBot
import com.kotlin.lex.deleteSpecificBot
import com.kotlin.lex.getAllBots
import com.kotlin.lex.getSlotsInfo
import com.kotlin.lex.getSpecificIntent
import com.kotlin.lex.getStatus
import kotlinx.coroutines.runBlocking
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
class LexTest {
    private var botName = ""
    private var intentName = ""
    private var intentVersion = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            botName = values.botName.toString()
            intentName = values.intentName.toString()
            intentVersion = values.intentVersion.toString()

            // val input = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
            // val prop = Properties()
            // prop.load(input)
            // botName = prop.getProperty("botName")
            // intentName = prop.getProperty("intentName")
            // intentVersion = prop.getProperty("intentVersion")
        }

    @Test
    @Order(1)
    fun putBotTest() =
        runBlocking {
            createBot(botName, intentName, intentVersion)
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun getBotsTest() =
        runBlocking {
            getAllBots()
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun getIntentTest() =
        runBlocking {
            getSpecificIntent(intentName, intentVersion)
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun getSlotTypesTest() =
        runBlocking {
            getSlotsInfo()
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun getBotStatusTest() =
        runBlocking {
            getStatus(botName)
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun deleteBotTest() =
        runBlocking {
            deleteSpecificBot(botName)
            println("Test 6 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/lex"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        SecretsManagerClient {
            region = "us-east-1"
            credentialsProvider = EnvironmentCredentialsProvider()
        }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/lex (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val intentName: String? = null
        val botName: String? = null
        val intentVersion: String? = null
    }
}
