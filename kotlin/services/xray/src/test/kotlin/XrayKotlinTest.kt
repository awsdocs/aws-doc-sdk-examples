// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
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
import java.util.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class XrayKotlinTest {
    private var groupName = ""
    private var newGroupName = ""
    private var ruleName = ""

    @BeforeAll
    fun setup() = runBlocking {
        val random = Random()
        val randomNum = random.nextInt((10000 - 1) + 1) + 1

        // Get the values to run these tests from AWS Secrets Manager.
        val gson = Gson()
        val json = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        groupName = values.groupName.toString()
        newGroupName = values.newGroupName.toString() + randomNum
        ruleName = values.ruleName.toString() + randomNum

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        groupName = prop.getProperty("groupName")
        newGroupName = prop.getProperty("newGroupName")
        ruleName = prop.getProperty("ruleName")
         */
    }

    @Test
    @Order(1)
    fun createGroup() = runBlocking {
        createNewGroup(newGroupName)
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun createSamplingRule() = runBlocking {
        createRule(ruleName)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun getGroups() = runBlocking {
        getAllGroups()
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun getSamplingRules() = runBlocking {
        getRules()
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun DeleteSamplingRule() = runBlocking {
        deleteRule(ruleName)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun DeleteGroup() = runBlocking {
        deleteSpecificGroup(newGroupName)
        println("Test 6 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/xray"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
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
