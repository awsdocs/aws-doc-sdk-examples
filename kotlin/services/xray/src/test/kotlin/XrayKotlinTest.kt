// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.xray.createNewGroup
import com.kotlin.xray.createRule
import com.kotlin.xray.deleteRule
import com.kotlin.xray.deleteSpecificGroup
import com.kotlin.xray.getAllGroups
import com.kotlin.xray.getRules
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class XrayKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(XrayKotlinTest::class.java)
    private var groupName = ""
    private var newGroupName = ""
    private var ruleName = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            val random = Random()
            val randomNum = random.nextInt((10000 - 1) + 1) + 1

            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            groupName = values.groupName.toString()
            newGroupName = values.newGroupName.toString() + randomNum
            ruleName = values.ruleName.toString() + randomNum
        }

    @Test
    @Order(1)
    fun createGroup() =
        runBlocking {
            createNewGroup(newGroupName)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createSamplingRule() =
        runBlocking {
            createRule(ruleName)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun getGroups() =
        runBlocking {
            getAllGroups()
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun getSamplingRules() =
        runBlocking {
            getRules()
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun deleteSamplingRule() =
        runBlocking {
            deleteRule(ruleName)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun deleteGroup() =
        runBlocking {
            deleteSpecificGroup(newGroupName)
            logger.info("Test 6 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/xray"
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
    @DisplayName("A class used to get test values from test/xray (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val groupName: String? = null
        val newGroupName: String? = null
        val ruleName: String? = null
    }
}
