// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.lex.getSlotsInfo
import com.kotlin.lex.getSpecificIntent
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
class LexTest {
    private val logger: Logger = LoggerFactory.getLogger(LexTest::class.java)
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
        }

    @Test
    @Order(1)
    fun getIntentTest() =
        runBlocking {
            getSpecificIntent(intentName, intentVersion)
            logger.info("Test 3 passed")
        }

    @Test
    @Order(2)
    fun getSlotTypesTest() =
        runBlocking {
            getSlotsInfo()
            logger.info("Test 4 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/lex"
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
    @DisplayName("A class used to get test values from test/lex (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val intentName: String? = null
        val botName: String? = null
        val intentVersion: String? = null
    }
}
