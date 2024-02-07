// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
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
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.Assertions.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class AppSyncTest {
    private var apiId = ""
    private var dsName = ""
    private var dsRole = ""
    private var tableName = ""
    private var keyId = ""

    @BeforeAll
    fun setup() = runBlocking {
        // Get the test values from AWS Secrets Manager.
        val gson = Gson()
        val json = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        apiId = values.apiId.toString()
        dsName = values.dsName.toString()
        dsRole = values.dsRole.toString()
        tableName = values.tableName.toString()

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        apiId = prop.getProperty("apiId")
        dsName = prop.getProperty("dsName")
        dsRole = prop.getProperty("dsRole")
        tableName = prop.getProperty("tableName")
        */
    }

    @Test
    @Order(1)
    fun CreateApiKey() = runBlocking {
        keyId = createKey(apiId).toString()
        assertTrue(!keyId.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun CreateDataSource() = runBlocking {
        val dsARN = createDS(dsName, dsRole, apiId, tableName)
        if (dsARN != null) {
            assertTrue(dsARN.isNotEmpty())
        }
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun GetDataSource() = runBlocking {
        getDS(apiId, dsName)
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun ListGraphqlApis() = runBlocking {
        getKeys(apiId)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun ListApiKeys() = runBlocking {
        getKeys(apiId)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun DeleteDataSource() = runBlocking {
        deleteDS(apiId, dsName)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun DeleteApiKey() = runBlocking {
        deleteKey(keyId, apiId)
        println("Test 7 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/appsync"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
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
