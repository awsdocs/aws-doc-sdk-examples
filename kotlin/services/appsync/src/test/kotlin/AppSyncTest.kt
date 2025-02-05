// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.example.appsync.createDS
import com.example.appsync.createKey
import com.example.appsync.deleteDS
import com.example.appsync.deleteKey
import com.example.appsync.getDS
import com.example.appsync.getKeys
import com.google.gson.Gson
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class AppSyncTest {
    private val logger: Logger = LoggerFactory.getLogger(AppSyncTest::class.java)
    private var apiId = ""
    private var dsName = ""
    private var dsRole = ""
    private var tableName = ""
    private var keyId = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get test values from AWS Secrets Manager.
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            apiId = values.apiId.toString()
            dsName = values.dsName.toString()
            dsRole = values.dsRole.toString()
            tableName = values.tableName.toString()
        }

    @Test
    @Order(1)
    fun createApiKey() =
        runBlocking {
            keyId = createKey(apiId).toString()
            assertTrue(!keyId.isEmpty())
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createDataSource() =
        runBlocking {
            val dsARN = createDS(dsName, dsRole, apiId, tableName)
            if (dsARN != null) {
                assertTrue(dsARN.isNotEmpty())
            }
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun getDataSource() =
        runBlocking {
            getDS(apiId, dsName)
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun listGraphqlApis() =
        runBlocking {
            getKeys(apiId)
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun listApiKeys() =
        runBlocking {
            getKeys(apiId)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun deleteDataSource() =
        runBlocking {
            deleteDS(apiId, dsName)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun deleteApiKey() =
        runBlocking {
            deleteKey(keyId, apiId)
            logger.info("Test 7 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/appsync"
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
    @DisplayName("A class used to get test values from test/appsync (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val apiId: String? = null
        val dsName: String? = null
        val dsRole: String? = null
        val tableName: String? = null
    }
}
