// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class STSTest {
    private val logger: Logger = LoggerFactory.getLogger(STSTest::class.java)
    private var roleArn = ""
    private var accessKeyId = ""
    private var roleSessionName = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            roleArn = values.roleArn.toString()
            accessKeyId = values.accessKeyId.toString()
            roleSessionName = values.roleSessionName.toString()
        }

    @Test
    @Order(1)
    fun assumeRoleTest() =
        runBlocking {
            assumeGivenRole(roleArn, roleSessionName)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun getSessionTokenTest() =
        runBlocking {
            getToken()
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun getCallerIdentityTest() =
        runBlocking {
            getCallerId()
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun getAccessKeyInfoTest() =
        runBlocking {
            getKeyInfo(accessKeyId)
            logger.info("Test 4 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/sts"
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
    @DisplayName("A class used to get test values from test/sts (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val roleArn: String? = null
        val accessKeyId: String? = null
        val roleSessionName: String? = null
    }
}
