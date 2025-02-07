// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//   SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.athena.createNamedQuery
import com.kotlin.athena.deleteQueryName
import com.kotlin.athena.listNamedQueries
import com.kotlin.athena.listQueryIds
import com.kotlin.athena.processResultRows
import com.kotlin.athena.submitAthenaQuery
import com.kotlin.athena.waitForQueryToComplete
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
class AthenaTest {
    private val logger: Logger = LoggerFactory.getLogger(AthenaTest::class.java)
    private var nameQuery: String? = null
    private var queryString: String? = null
    private var database: String? = null
    private var outputLocation: String? = null
    private var queryId: String? = null

    @BeforeAll
    fun setup() = runBlocking {
        // Get the values from AWS Secrets Manager.
        val gson = Gson()
        val json = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        nameQuery = values.nameQuery.toString()
        queryString = values.queryString.toString()
        database = values.database.toString()
        outputLocation = values.outputLocation.toString()
    }

    @Test
    @Order(1)
    fun createNamedQueryTest() = runBlocking {
        queryId = createNamedQuery(queryString.toString(), nameQuery.toString(), database.toString())
        queryId?.let { assertTrue(it.isNotEmpty()) }
        logger.info("Test 1 passed")
    }

    @Test
    @Order(2)
    fun listNamedQueryTest() = runBlocking {
        listNamedQueries()
        logger.info("Test 2 passed")
    }

    @Test
    @Order(3)
    fun listQueryExecutionsTest() = runBlocking {
        listQueryIds()
        logger.info("Test 3 passed")
    }

    @Test
    @Order(4)
    fun startQueryExampleTest() = runBlocking {
        val queryExecutionId = submitAthenaQuery(queryString.toString(), database.toString(), outputLocation.toString())
        waitForQueryToComplete(queryExecutionId)
        processResultRows(queryExecutionId)
        logger.info("Test 4 passed")
    }

    @Test
    @Order(5)
    fun deleteNamedQueryTest() = runBlocking {
        deleteQueryName(queryId)
        logger.info("Test 5 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/athena"
        val valueRequest = GetSecretValueRequest {
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
        val nameQuery: String? = null
        val queryString: String? = null
        val outputLocation: String? = null
        val database: String? = null
    }
}
