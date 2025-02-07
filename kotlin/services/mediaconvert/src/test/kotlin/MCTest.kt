// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.mediaconvert.MediaConvertClient
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.mediaconvert.createMediaJob
import com.kotlin.mediaconvert.getSpecificJob
import com.kotlin.mediaconvert.listCompleteJobs
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
import java.io.IOException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class MCTest {
    private val logger: Logger = LoggerFactory.getLogger(MCTest::class.java)
    lateinit var mcClient: MediaConvertClient
    private var mcRoleARN = ""
    private var fileInput = ""
    private var jobId = ""

    @BeforeAll
    @Throws(IOException::class)
    fun setUp() =
        runBlocking {
            mcClient = MediaConvertClient { region = "us-west-2" }
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            mcRoleARN = values.mcRoleARN.toString()
            fileInput = values.fileInput.toString()
        }

    @Test
    @Order(1)
    fun createJobTest() =
        runBlocking {
            jobId = createMediaJob(mcClient, mcRoleARN, fileInput).toString()
            assertTrue(!jobId.isEmpty()).toString()
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun listJobsTest() =
        runBlocking {
            listCompleteJobs(mcClient)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun getJobTest() =
        runBlocking {
            getSpecificJob(mcClient, jobId)
            logger.info("Test 3 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/mediaconvert"
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
    @DisplayName("A class used to get test values from test/mediaconvert (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val mcRoleARN: String? = null
        val fileInput: String? = null
    }
}
