// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.pipeline.createNewPipeline
import com.kotlin.pipeline.deleteSpecificPipeline
import com.kotlin.pipeline.executePipeline
import com.kotlin.pipeline.getAllPipelines
import com.kotlin.pipeline.getSpecificPipeline
import com.kotlin.pipeline.listExecutions
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class PipelineServiceTest {
    private var name: String = ""
    private var roleArn: String = ""
    private var s3Bucket: String = ""
    private var s3OuputBucket: String = ""

    @BeforeAll
    fun setup() = runBlocking {

        // Get values from AWS Secrets Manager.
        val gson = Gson()
        val json: String = getSecretValues()
        val values: SecretValues = gson.fromJson(json, SecretValues::class.java)
        name = values.name.toString()
        roleArn = values.role.toString()
        s3Bucket = values.s3Bucket.toString()
        s3OuputBucket = values.s3OuputBucket.toString()

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // load the properties file.
        prop.load(input)

        // Populate the data members required for all tests.
        name = prop.getProperty("name")
        roleArn = prop.getProperty("roleArn")
        s3Bucket = prop.getProperty("s3Bucket")
        s3OuputBucket = prop.getProperty("s3OuputBucket")
        */
}

@Test
@Order(1)
fun createPipelineTest() = runBlocking {
 createNewPipeline(name, roleArn, s3Bucket, s3OuputBucket)
 println("\n Test 1 passed")
}

@Test
@Order(2)
fun startPipelineExecutionTest() = runBlocking {
 executePipeline(name)
 println("Test 2 passed")
}

@Test
@Order(3)
fun listPipelinesTest() = runBlocking {
 getAllPipelines()
 println("Test 3 passed")
}

@Test
@Order(4)
fun getPipelineTest() = runBlocking {
 getSpecificPipeline(name)
 println("Test 4 passed")
}

@Test
@Order(5)
fun listPipelineExecutionsTest() = runBlocking {
 listExecutions(name)
 println("Test 5 passed")
}

@Test
@Order(6)
fun deletePipelineTest() = runBlocking {
 deleteSpecificPipeline(name)
 println("Test 6 passed")
}

private suspend fun getSecretValues(): String {
 val secretName = "test/pipeline"
 val valueRequest = GetSecretValueRequest {
     secretId = secretName
 }
 SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
     val valueResponse = secretClient.getSecretValue(valueRequest)
     return valueResponse.secretString.toString()
 }
}

@Nested
@DisplayName("A class used to get test values from test/apigateway (an AWS Secrets Manager secret)")
internal class SecretValues {
 val name: String? = null
 val role: String? = null
 val s3Bucket: String? = null
 val s3OuputBucket: String? = null
}
}
