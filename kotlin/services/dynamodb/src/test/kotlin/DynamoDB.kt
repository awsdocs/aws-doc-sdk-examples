// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.dynamodb.createNewTable
import com.kotlin.dynamodb.deleteDynamoDBTable
import com.kotlin.dynamodb.describeDymamoDBTable
import com.kotlin.dynamodb.getSpecificItem
import com.kotlin.dynamodb.listAllTables
import com.kotlin.dynamodb.putItemInTable
import com.kotlin.dynamodb.queryDynTable
import com.kotlin.dynamodb.scanItems
import com.kotlin.dynamodb.scenario.createScenarioTable
import com.kotlin.dynamodb.scenario.createTablePartiQL
import com.kotlin.dynamodb.scenario.createTablePartiQLBatch
import com.kotlin.dynamodb.scenario.deletIssuesTable
import com.kotlin.dynamodb.scenario.deleteItemsBatch
import com.kotlin.dynamodb.scenario.deleteTablePartiQL
import com.kotlin.dynamodb.scenario.deleteTablePartiQLBatch
import com.kotlin.dynamodb.scenario.getMovie
import com.kotlin.dynamodb.scenario.getMoviePartiQL
import com.kotlin.dynamodb.scenario.loadData
import com.kotlin.dynamodb.scenario.loadDataPartiQL
import com.kotlin.dynamodb.scenario.putRecordBatch
import com.kotlin.dynamodb.scenario.putRecordPartiQL
import com.kotlin.dynamodb.scenario.queryTablePartiQL
import com.kotlin.dynamodb.scenario.scanMovies
import com.kotlin.dynamodb.scenario.updateTableItemBatchBatch
import com.kotlin.dynamodb.scenario.updateTableItemPartiQL
import com.kotlin.dynamodb.updateTableItem
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
class DynamoDB {
    private val logger: Logger = LoggerFactory.getLogger(DynamoDB::class.java)
    var tableName: String = ""
    var fileName: String = ""
    var tableName2: String = ""
    var key: String = ""
    var keyValue: String = ""
    var albumTitle: String = ""
    var albumTitleValue: String = ""
    var awards: String = ""
    var awardVal: String = ""
    var songTitle: String = ""
    var songTitleVal: String = ""
    var modAwardVal: String = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values from AWS Secrets Manager.
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            tableName = values.tableName.toString()
            fileName = values.fileName.toString()
            key = values.key.toString()
            keyValue = values.keyValue.toString()
            albumTitle = values.albumTitle.toString()
            albumTitleValue = values.albumTitleValue.toString()
            awards = values.awards.toString()
            awardVal = values.getAwardVal().toString()
            songTitle = values.songTitleVal.toString()
            songTitleVal = values.songTitleVal.toString()
            tableName2 = "Movies"
        }

    @Test
    @Order(1)
    fun createTableTest() =
        runBlocking {
            createNewTable(tableName, key)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun describeTableTest() =
        runBlocking {
            describeDymamoDBTable(tableName)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun putItemTest() =
        runBlocking {
            putItemInTable(
                tableName,
                key,
                keyValue,
                albumTitle,
                albumTitleValue,
                awards,
                awardVal,
                songTitle,
                songTitleVal
            )
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun listTablesTest() =
        runBlocking {
            listAllTables()
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun updateItemTest() =
        runBlocking {
            updateTableItem(tableName, key, keyValue, awards, modAwardVal)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun getItemTest() =
        runBlocking {
            getSpecificItem(tableName, key, keyValue)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun queryTableTest() =
        runBlocking {
            queryDynTable(tableName, key, keyValue, "#a")
            logger.info("Test 7 passed")
        }

    @Test
    @Order(8)
    fun dynamoDBScanTest() =
        runBlocking {
            scanItems(tableName)
            logger.info("Test 8 passed")
        }

    @Test
    @Order(9)
    fun deleteItemTest() =
        runBlocking {
            com.kotlin.dynamodb.deleteDynamoDBItem(tableName, key, keyValue)
            logger.info("Test 9 passed")
        }

    @Test
    @Order(10)
    fun deleteTableTest() =
        runBlocking {
            deleteDynamoDBTable(tableName)
            logger.info("Test 10 passed")
        }

    @Test
    @Order(11)
    fun testScenario() =
        runBlocking {
            createScenarioTable(tableName2, "year")
            loadData(tableName2, fileName)
            getMovie(tableName2, "year", "1933")
            scanMovies(tableName2)
            deletIssuesTable(tableName2)
            logger.info("Test 11 passed")
        }

    @Test
    @Order(12)
    fun testScenarioPartiQ() =
        runBlocking {
            val tableNamePartiQ = "MoviesPartiQ"
            val ddb = DynamoDbClient { region = "us-east-1" }
            createTablePartiQL(ddb, tableNamePartiQ, "year")
            loadDataPartiQL(ddb, fileName)
            getMoviePartiQL(ddb)
            putRecordPartiQL(ddb)
            updateTableItemPartiQL(ddb)
            queryTablePartiQL(ddb)
            deleteTablePartiQL(tableNamePartiQ)
            logger.info("Test 12 passed")
        }

    @Test
    @Order(13)
    fun testScenarioPartiQBatch() =
        runBlocking {
            val tableNamePartiQBatch = "MoviesPartiQBatch"
            val ddb = DynamoDbClient { region = "us-east-1" }
            println("Creating an Amazon DynamoDB table named $tableNamePartiQBatch with a key named id and a sort key named title.")
            createTablePartiQLBatch(ddb, tableNamePartiQBatch, "year")
            putRecordBatch(ddb)
            updateTableItemBatchBatch(ddb)
            deleteItemsBatch(ddb)
            deleteTablePartiQLBatch(tableNamePartiQBatch)
            logger.info("Test 13 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretClient =
            SecretsManagerClient {
                region = "us-east-1"
            }
        val secretName = "test/dynamodb"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        val valueResponse = secretClient.getSecretValue(valueRequest)
        return valueResponse.secretString.toString()
    }

    @Nested
    @DisplayName("A class used to get test values from test/dynamodb (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val tableName: String? = null
        val key: String? = null
        val keyValue: String? = null
        val albumTitle: String? = null
        val albumTitleValue: String? = null
        val awards: String? = null
        private val awardVal: String? = null
        private val songTitle: String? = null
        val songTitleVal: String? = null
        val fileName: String? = null

        fun getAwardVal(): String? = songTitle
    }
}
