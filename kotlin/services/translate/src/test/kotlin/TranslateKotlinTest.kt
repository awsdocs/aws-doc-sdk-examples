// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.translate.describeTranslationJob
import com.kotlin.translate.getTranslationJobs
import com.kotlin.translate.textTranslate
import com.kotlin.translate.translateDocuments
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
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
class TranslateKotlinTest {
    private var s3Uri = ""
    private var s3UriOut = ""
    private var jobName = ""
    private var dataAccessRoleArn = ""
    private var jobId = ""

    @BeforeAll
    fun setup() = runBlocking {
        // Get the values to run these tests from AWS Secrets Manager.
        val gson = Gson()
        val json: String = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        s3Uri = values.s3Uri.toString()
        s3UriOut = values.s3UriOut.toString()
        jobName = values.jobName.toString() + UUID.randomUUID()
        dataAccessRoleArn = values.dataAccessRoleArn.toString()

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)

        // Populate the data members required for all tests.
        s3Uri = prop.getProperty("s3Uri")
        s3UriOut = prop.getProperty("s3UriOut")
        jobName = prop.getProperty("jobName")
        dataAccessRoleArn = prop.getProperty("dataAccessRoleArn")
         */
    }

    @Test
    @Order(1)
    fun translateTextTest() = runBlocking {
        textTranslate()
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun batchTranslationTest() = runBlocking {
        jobId = translateDocuments(s3Uri, s3UriOut, jobName, dataAccessRoleArn).toString()
        Assertions.assertTrue(!jobId.isEmpty())
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun listTextTranslationJobsTest() = runBlocking {
        getTranslationJobs()
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun describeTextTranslationJobTest() = runBlocking {
        describeTranslationJob(jobId)
        println("Test 4 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/translate"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/translate (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val s3Uri: String? = null
        val s3UriOut: String? = null
        val jobName: String? = null
        val dataAccessRoleArn: String? = null
    }
}
