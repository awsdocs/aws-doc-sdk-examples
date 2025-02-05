// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.lambda.createNewFunction
import com.kotlin.lambda.createScFunction
import com.kotlin.lambda.delFunction
import com.kotlin.lambda.delLambdaFunction
import com.kotlin.lambda.getFunction
import com.kotlin.lambda.getSettings
import com.kotlin.lambda.invokeFunctionSc
import com.kotlin.lambda.listFunctions
import com.kotlin.lambda.listFunctionsSc
import com.kotlin.lambda.updateFunctionCode
import com.kotlin.lambda.updateFunctionConfiguration
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class LambdaTest {
    private val logger: Logger = LoggerFactory.getLogger(LambdaTest::class.java)
    var functionName: String = ""
    var functionARN: String = "" // Gets set in a test.
    var s3BucketName: String = ""
    var updatedBucketName: String = ""
    var functionNameSc: String = ""
    var s3Key: String = ""
    var role: String = ""
    var handler: String = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            functionName = values.functionName.toString() + UUID.randomUUID()
            functionNameSc = values.functionNameSc.toString() + UUID.randomUUID()
            role = values.role.toString()
            handler = values.handler.toString()
            functionNameSc = values.functionNameSc.toString() + UUID.randomUUID()
            s3BucketName = values.bucketName.toString()
            updatedBucketName = values.bucketName2.toString()
            s3Key = values.key.toString()
        }

    @Test
    @Order(1)
    fun createFunctionTest() =
        runBlocking {
            functionARN = createNewFunction(functionName, s3BucketName, s3Key, handler, role).toString()
            Assertions.assertTrue(!functionARN.isEmpty())
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun listLambdaTest() =
        runBlocking {
            listFunctions()
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun getAccountSettings() =
        runBlocking {
            getSettings()
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun deleteFunctionTest() =
        runBlocking {
            delLambdaFunction(functionName)
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun testLambdaScenario() =
        runBlocking {
            println("Creating a Lambda function named $functionNameSc.")
            val funArn = createScFunction(functionNameSc, s3BucketName, s3Key, handler, role)
            println("The AWS Lambda ARN is $funArn")

            // Get a specific Lambda function.
            println("Getting the $functionNameSc AWS Lambda function.")
            getFunction(functionNameSc)

            // List the Lambda functions.
            println("Listing all AWS Lambda functions.")
            listFunctionsSc()

            // Invoke the Lambda function.
            println("*** Invoke the Lambda function.")
            invokeFunctionSc(functionNameSc)

            // Update the AWS Lambda function code.
            println("*** Update the Lambda function code.")
            updateFunctionCode(functionNameSc, s3BucketName, s3Key)

            // println("*** Invoke the function again after updating the code.")
            invokeFunctionSc(functionNameSc)

            // Update the AWS Lambda function configuration.
            println("Update the run time of the function.")
            updateFunctionConfiguration(functionNameSc, handler)

            // Delete the AWS Lambda function.
            println("Delete the AWS Lambda function.")
            delFunction(functionNameSc)
            logger.info("Test 5 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/lambda"
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
    @DisplayName("A class used to get test values from test/lambda (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val filePath: String? = null
        val role: String? = null
        val handler: String? = null
        val functionNameSc: String? = null
        val bucketName: String? = null
        val bucketName2: String? = null
        val key: String? = null
        val functionName: String? = null
    }
}
