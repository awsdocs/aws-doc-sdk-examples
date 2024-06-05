// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.glue.getAllCrawlers
import com.kotlin.glue.getAllDatabases
import com.kotlin.glue.listAllWorkflows
import com.kotlin.glue.searchGlueTable
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class GlueTest {
    private var cron = ""
    private var iam = ""
    private var tableName = ""
    private var text = ""
    private var jobNameSc = ""
    private var s3PathSc = ""
    private var dbNameSc = ""
    private var crawlerNameSc = ""
    private var scriptLocationSc = ""
    private var locationUri = ""

    @BeforeAll
    fun setup() = runBlocking {
        // Get the values to run these tests from AWS Secrets Manager.
        val gson = Gson()
        val json: String = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        crawlerNameSc = values.crawlerName.toString()
        s3PathSc = values.s3Path.toString()
        cron = values.cron.toString()
        tableName = values.tableName.toString()
        iam = values.iAM.toString()
        text = values.text.toString()
        jobNameSc = values.jobNameSc.toString() + UUID.randomUUID()
        s3PathSc = values.s3PathSc.toString()
        dbNameSc = values.dbNameSc.toString() + UUID.randomUUID()
        crawlerNameSc = values.crawlerNameSc.toString() + UUID.randomUUID()
        scriptLocationSc = values.scriptLocationSc.toString()
        locationUri = values.locationUri.toString()

        // Uncomment the block below if using config.properties file
        /*
        val input = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        cron = prop.getProperty("cron")
        iam = prop.getProperty("IAM")
        tableName = prop.getProperty("tableName")
        text = prop.getProperty("text")
        jobNameSc = prop.getProperty("jobNameSc")
        s3PathSc = prop.getProperty("s3PathSc")
        dbNameSc = prop.getProperty("dbNameSc")
        crawlerNameSc = prop.getProperty("crawlerNameSc")
        scriptLocationSc = prop.getProperty("scriptLocationSc")
        locationUri = prop.getProperty("locationUri")
         */
    }

    @Test
    @Order(2)
    fun getCrawlersTest() = runBlocking {
        getAllCrawlers()
        println("Test 2 passed")
    }

    @Test
    @Order(4)
    fun getDatabasesTest() = runBlocking {
        getAllDatabases()
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun searchTablesTest() = runBlocking {
        searchGlueTable(text)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun listWorkflowsTest() = runBlocking {
        listAllWorkflows()
        println("Test 6 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/glue"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/glue (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val iAM: String? = null
        val s3Path: String? = null
        val cron: String? = null
        val crawlerName: String? = null
        val tableName: String? = null
        val text: String? = null
        val jobNameSc: String? = null
        val dbNameSc: String? = null
        val crawlerNameSc: String? = null
        val s3PathSc: String? = null
        val scriptLocationSc: String? = null
        val locationUri: String? = null
    }
}
