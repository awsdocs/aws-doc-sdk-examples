/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import aws.sdk.kotlin.services.kendra.KendraClient
import com.example.kendra.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.IOException
import java.util.*

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
    fun setup() {

        try {
            KendraTest::class.java.classLoader.getResourceAsStream("config.properties").use { input ->
                val prop = Properties()
                if (input == null) {
                    println("Sorry, unable to find config.properties")
                    return
                }

                // Load a properties file from the class path.
                prop.load(input)

                // Populate the data members required for all tests.
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
    }

    @Test
    @Order(1)
    fun whenInitializingAWSService_thenNotNull() {
        assertTrue(!indexName.isEmpty())
        assertTrue(!indexRoleArn.isEmpty())
        assertTrue(!indexDescription.isEmpty())
        assertTrue(!s3BucketName.isEmpty())
        assertTrue(!dataSourceName.isEmpty())
        assertTrue(!dataSourceDescription.isEmpty())
        assertTrue(!dataSourceRoleArn.isEmpty())
        assertTrue(!text.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun CreateIndex() = runBlocking {
        indexId = createIndex(indexDescription, indexName, indexRoleArn)
        assertTrue(!indexId.isEmpty())
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun CreateDataSource() = runBlocking {
        dataSourceId= createDataSource(s3BucketName, dataSourceName, dataSourceDescription, indexId, dataSourceRoleArn)
        assertTrue(!dataSourceId.isEmpty())
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun SyncDataSource() = runBlocking {
        startDataSource(indexId,dataSourceId)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun ListSyncJobs() = runBlocking {
        listSyncJobs(indexId, dataSourceId)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun QueryIndex() = runBlocking {
        querySpecificIndex(indexId, text)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun DeleteDataSource() = runBlocking {
        deleteSpecificDataSource(indexId, dataSourceId)
        println("Test 7 passed")
    }

    @Test
    @Order(8)
    fun DeleteIndex() = runBlocking {
        deleteSpecificIndex(indexId)
        println("Test 8 passed")
    }
}
