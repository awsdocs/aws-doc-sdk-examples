// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.sts.assumeGivenRole
import com.kotlin.sts.getCallerId
import com.kotlin.sts.getKeyInfo
import com.kotlin.sts.getToken
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
class Test2 {
    private var roleArn = ""
    private var accessKeyId = ""
    private var roleSessionName = ""

    @BeforeAll
    fun setup() = runBlocking {
        // Get the values to run these tests from AWS Secrets Manager.
        val gson = Gson()
        val json: String = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        roleArn = values.roleArn.toString()
        accessKeyId = values.accessKeyId.toString()
        roleSessionName = values.roleSessionName.toString()

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)

        // Populate the data members required for all tests.
        roleArn = prop.getProperty("roleArn")
        accessKeyId = prop.getProperty("accessKeyId")
        roleSessionName = prop.getProperty("roleSessionName")
         */
    }

    @Test
    @Order(2)
    fun assumeRoleTest() = runBlocking {
        assumeGivenRole(roleArn, roleSessionName)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun getSessionTokenTest() = runBlocking {
        getToken()
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun getCallerIdentityTest() = runBlocking {
        getCallerId()
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun getAccessKeyInfoTest() = runBlocking {
        getKeyInfo(accessKeyId)
        println("Test 5 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/sts"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/sts (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val roleArn: String? = null
        val accessKeyId: String? = null
        val roleSessionName: String? = null
    }
}
