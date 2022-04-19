/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.appsync.createDS
import com.example.appsync.createKey
import com.example.appsync.deleteDS
import com.example.appsync.deleteKey
import com.example.appsync.getDS
import com.example.appsync.getKeys
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import java.util.Properties

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class AppSyncTest {

    private var apiId = ""
    private var dsName = ""
    private var dsRole = ""
    private var tableName = ""
    private var keyId = "" // gets dynamically set in a test.
    private var dsARN = "" // gets dynamically set in a test.

    @BeforeAll
    fun setup() {

        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        apiId = prop.getProperty("apiId")
        dsName = prop.getProperty("dsName")
        dsRole = prop.getProperty("dsRole")
        tableName = prop.getProperty("tableName")
    }

    @Test
    @Order(1)
    fun whenInitializingAWSService_thenNotNull() = runBlocking {
        assertTrue(!apiId.isEmpty())
        assertTrue(!dsName.isEmpty())
        assertTrue(!dsRole.isEmpty())
        assertTrue(!tableName.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun CreateApiKey() = runBlocking {
        keyId = createKey(apiId).toString()
        assertTrue(!keyId.isEmpty())
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun CreateDataSource() = runBlocking {
        val dsARN = createDS(dsName, dsRole, apiId, tableName)
        if (dsARN != null) {
            assertTrue(!dsARN.isEmpty())
        }
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun GetDataSource() = runBlocking {
        getDS(apiId, dsName)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun ListGraphqlApis() = runBlocking {
        getKeys(apiId)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun ListApiKeys() = runBlocking {
        getKeys(apiId)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun DeleteDataSource() = runBlocking {
        deleteDS(apiId, dsName)
        println("Test 7 passed")
    }

    @Test
    @Order(8)
    fun DeleteApiKey() = runBlocking {
        deleteKey(keyId, apiId)
        println("Test 8 passed")
    }
}
