// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.sage.createSagemakerModel
import com.kotlin.sage.deleteSagemakerModel
import com.kotlin.sage.describeTrainJob
import com.kotlin.sage.listAlgs
import com.kotlin.sage.listAllModels
import com.kotlin.sage.listBooks
import com.kotlin.sage.listJobs
import com.kotlin.sage.trainJob
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
class SageMakerTest {
    private var image = ""
    private var modelDataUrl = ""
    private var executionRoleArn = ""
    private var modelName = ""
    private var s3UriData = ""
    private var s3Uri = ""
    private var trainingJobName = ""
    private var roleArn = ""
    private var s3OutputPath = ""
    private var channelName = ""
    private var trainingImage = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            image = values.image.toString()
            modelDataUrl = values.modelDataUrl.toString()
            executionRoleArn = values.executionRoleArn.toString()
            modelName = values.modelName.toString() + UUID.randomUUID()
            s3UriData = values.s3UriData.toString()
            s3Uri = values.s3Uri.toString()
            roleArn = values.roleArn.toString()
            trainingJobName = values.trainingJobName.toString() + UUID.randomUUID()
            s3OutputPath = values.s3OutputPath.toString()
            channelName = values.channelName.toString()
            trainingImage = values.trainingImage.toString()

            // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        image = prop.getProperty("image")
        modelDataUrl = prop.getProperty("modelDataUrl")
        executionRoleArn = prop.getProperty("executionRoleArn")
        modelName = prop.getProperty("modelName")
        s3UriData = prop.getProperty("s3UriData")
        s3Uri = prop.getProperty("s3Uri")
        roleArn = prop.getProperty("roleArn")
        trainingJobName = prop.getProperty("trainingJobName")
        s3OutputPath = prop.getProperty("s3OutputPath")
        channelName = prop.getProperty("channelName")
        trainingImage = prop.getProperty("trainingImage")
        s3UriTransform = prop.getProperty("s3UriTransform")
        s3OutputPathTransform = prop.getProperty("s3OutputPathTransform")
        transformJobName = prop.getProperty("transformJobName")
         */
        }

    @Test
    @Order(1)
    fun createModelTest() =
        runBlocking {
            createSagemakerModel(modelDataUrl, image, modelName, executionRoleArn)
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createTrainingJobTest() =
        runBlocking {
            trainJob(s3UriData, s3Uri, trainingJobName, roleArn, s3OutputPath, channelName, trainingImage)
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun describeTrainingJobTest() =
        runBlocking {
            describeTrainJob(trainingJobName)
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun listModelsTest() =
        runBlocking {
            listAllModels()
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun listNotebooksTest() =
        runBlocking {
            listBooks()
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun listAlgorithmsTest() =
        runBlocking {
            listAlgs()
            println("Test 6 passed")
        }

    @Test
    @Order(7)
    fun listTrainingJobsTest() =
        runBlocking {
            listJobs()
            println("Test 7 passed")
        }

    @Test
    @Order(8)
    fun deleteModelTest() =
        runBlocking {
            deleteSagemakerModel(modelName)
            println("Test 8 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/sagemaker"
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
    @DisplayName("A class used to get test values from test/sagemaker (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val trainingJobName: String? = null
        val modelName: String? = null
        val image: String? = null
        val modelDataUrl: String? = null
        val executionRoleArn: String? = null
        val s3UriData: String? = null
        val s3Uri: String? = null
        val roleArn: String? = null
        val s3OutputPath: String? = null
        val channelName: String? = null
        val trainingImage: String? = null
    }
}
