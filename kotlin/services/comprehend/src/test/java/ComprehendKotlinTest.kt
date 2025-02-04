// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.comprehend.detectAllEntities
import com.kotlin.comprehend.detectAllKeyPhrases
import com.kotlin.comprehend.detectAllSyntax
import com.kotlin.comprehend.detectSentiments
import com.kotlin.comprehend.detectTheDominantLanguage
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
class ComprehendKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(ComprehendKotlinTest::class.java)
    private val text =
"""
Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders.
Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing
"""
    private val frText = "Il pleut aujourd'hui à Seattle"
    private var dataAccessRoleArn = ""
    private var s3Uri = ""
    private var documentClassifierName = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            dataAccessRoleArn = values.dataAccessRoleArn.toString()
            s3Uri = values.s3Uri.toString()
            documentClassifierName = values.documentClassifier.toString()
        }

    @Test
    @Order(1)
    fun detectEntitiesTest() =
        runBlocking {
            detectAllEntities(text)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun detectKeyPhrasesTest() =
        runBlocking {
            detectAllKeyPhrases(text)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun detectLanguageTest() =
        runBlocking {
            detectTheDominantLanguage(frText)
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun detectSentimentTest() =
        runBlocking {
            detectSentiments(text)
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun detectSyntaxTest() =
        runBlocking {
            detectAllSyntax(text)
            logger.info("Test 5 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretClient =
            SecretsManagerClient {
                region = "us-east-1"
            }
        val secretName = "test/comprehend"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        val valueResponse = secretClient.getSecretValue(valueRequest)
        return valueResponse.secretString.toString()
    }

    @Nested
    @DisplayName("A class used to get test values from test/comprehend (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val dataAccessRoleArn: String? = null
        val s3Uri: String? = null
        val documentClassifier: String? = null
    }
}
