// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.kendra.KendraClient
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.example.kendra.createDataSource
import com.example.kendra.createIndex
import com.example.kendra.deleteSpecificDataSource
import com.example.kendra.deleteSpecificIndex
import com.example.kendra.listSyncJobs
import com.example.kendra.querySpecificIndex
import com.example.kendra.startDataSource
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class KendraTest {
    private var kendra: KendraClient? = null
    private var indexName = ""
    private var indexDescription = ""
    private var indexRoleArn = ""
    private var indexId = ""
    private var s3BucketName = ""
    private var dataSourceName = ""
    private var dataSourceDescription = ""
    private var dataSourceRoleArn = ""
    private var dataSourceId = ""
    private var text = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            indexName = values.indexName.toString() + UUID.randomUUID()
            indexRoleArn = values.indexRoleArn.toString()
            indexDescription = values.indexDescription.toString()
            s3BucketName = values.s3BucketName.toString()
            dataSourceName = values.dataSourceName.toString()
            dataSourceDescription = values.dataSourceDescription.toString()
            dataSourceRoleArn = values.dataSourceRoleArn.toString()
            text = values.text.toString()

        /*
        try {
            KendraTest::class.java.classLoader.getResourceAsStream("config.properties").use { input ->
                val prop = Properties()
                if (input == null) {
                    println("Sorry, unable to find config.properties")
                    return
                }
                prop.load(input)
                indexName = prop.getProperty("indexName")
                indexRoleArn = prop.getProperty("indexRoleArn")
                indexDescription = prop.getProperty("indexDescription")
                s3BucketName = prop.getProperty("s3BucketName")
                dataSourceName = prop.getProperty("dataSourceName")
                dataSourceDescription = prop.getProperty("dataSourceDescription")
                dataSourceRoleArn = prop.getProperty("dataSourceRoleArn")
                text = prop.getProperty("text")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
         */
        }

    @Test
    @Order(1)
    fun createIndex() =
        runBlocking {
            indexId = createIndex(indexDescription, indexName, indexRoleArn)
            assertTrue(!indexId.isEmpty())
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createDataSource() =
        runBlocking {
            dataSourceId = createDataSource(s3BucketName, dataSourceName, dataSourceDescription, indexId, dataSourceRoleArn)
            assertTrue(!dataSourceId.isEmpty())
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun syncDataSource() =
        runBlocking {
            startDataSource(indexId, dataSourceId)
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun listSyncJobs() =
        runBlocking {
            listSyncJobs(indexId, dataSourceId)
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun queryIndex() =
        runBlocking {
            querySpecificIndex(indexId, text)
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun deleteDataSource() =
        runBlocking {
            deleteSpecificDataSource(indexId, dataSourceId)
            println("Test 6 passed")
        }

    @Test
    @Order(7)
    fun deleteIndex() =
        runBlocking {
            deleteSpecificIndex(indexId)
            println("Test 7 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/kendra"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        SecretsManagerClient {
            region = "us-east-1"
            credentialsProvider = EnvironmentCredentialsProvider()
        }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/kendra (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val indexName: String? = null
        val dataSourceName: String? = null
        val indexDescription: String? = null
        val indexRoleArn: String? = null
        val s3BucketName: String? = null
        val dataSourceDescription: String? = null
        val text: String? = null
        val dataSourceRoleArn: String? = null
    }
}
