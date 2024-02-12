// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.secrets.createNewSecret
import com.kotlin.secrets.deleteSpecificSecret
import com.kotlin.secrets.describeGivenSecret
import com.kotlin.secrets.getValue
import com.kotlin.secrets.listAllSecrets
import com.kotlin.secrets.updateMySecret
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
import java.util.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class SecretsManagerKotlinTest {
    private var newSecretName = ""
    private var secretValue = ""
    private var secretARN = ""
    private var modSecretValue = ""

    @BeforeAll
    fun setup() = runBlocking {
        val random = Random()
        val randomNum = random.nextInt(10000 - 1 + 1) + 1
        // Get the values to run these tests from AWS Secrets Manager.
        val gson = Gson()
        val json = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        newSecretName = values.newSecretName.toString() + randomNum
        secretValue = values.secretValue.toString()
        modSecretValue = values.modSecretValue.toString()

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // load the properties file.
        prop.load(input)
        newSecretName = prop.getProperty("newSecretName")
        secretValue = prop.getProperty("secretValue")
        */
    }

    @Test
    @Order(1)
    fun createSecret() = runBlocking {
        secretARN = createNewSecret(newSecretName, secretValue).toString()
        assertTrue(!secretARN.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun describeSecret() = runBlocking {
        describeGivenSecret(newSecretName)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun getSecretValue() = runBlocking {
        getValue(newSecretName)
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun updateSecret() = runBlocking {
        updateMySecret(newSecretName, secretValue)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun listSecrets() = runBlocking {
        listAllSecrets()
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun deleteSecret() = runBlocking {
        deleteSpecificSecret(newSecretName)
        println("Test 6 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/secretmanager"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/secretmanager (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val newSecretName: String? = null
        val secretValue: String? = null
        val modSecretValue: String? = null
    }
}
