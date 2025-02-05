// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.cloudformation.createCFStack
import com.kotlin.cloudformation.deleteSpecificTemplate
import com.kotlin.cloudformation.describeAllStacks
import com.kotlin.cloudformation.getSpecificTemplate
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
class CloudFormationTest {
    private val logger: Logger = LoggerFactory.getLogger(CloudFormationTest::class.java)
    private var stackName = ""
    private var roleARN = ""
    private var location = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            stackName = values.stackName.toString()
            roleARN = values.roleARN.toString()
            location = values.location.toString()
        }

    @Test
    @Order(1)
    fun createStackTest() =
        runBlocking {
            createCFStack(stackName, roleARN, location)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun describeStacksTest() =
        runBlocking {
            describeAllStacks()
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun getTemplateTest() =
        runBlocking {
            getSpecificTemplate(stackName)
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun deleteStackTest() =
        runBlocking {
            deleteSpecificTemplate(stackName)
            logger.info("Test 4 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/cloudformation"
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
    @DisplayName("A class used to get test values from test/cloudformation (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val stackName: String? = null
        val roleARN: String? = null
        val location: String? = null
        val key: String? = null
        val value: String? = null
    }
}
