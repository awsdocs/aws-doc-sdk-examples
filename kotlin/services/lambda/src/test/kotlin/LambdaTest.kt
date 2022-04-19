/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.kotlin.lambda.UpdateFunctionConfiguration
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
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
class LambdaTest {

    var functionName: String = ""
    var functionARN: String = "" // Gets set in a test.
    var s3BucketName: String = ""
    var updatedBucketName: String = ""
    var functionNameSc: String = ""
    var s3Key: String = ""
    var role: String = ""
    var handler: String = ""

    @BeforeAll
    fun setup() {

        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        prop.load(input)
        functionName = prop.getProperty("functionName")
        functionNameSc = prop.getProperty("functionNameSc")
        s3BucketName = prop.getProperty("s3BucketName")
        updatedBucketName = prop.getProperty("updatedBucketName")
        s3Key = prop.getProperty("s3Key")
        role = prop.getProperty("role")
        handler = prop.getProperty("handler")
    }

    @Test
    @Order(1)
    fun whenInitializingAWSService_thenNotNull() {
        Assertions.assertTrue(!functionName.isEmpty())
        Assertions.assertTrue(!s3BucketName.isEmpty())
        Assertions.assertTrue(!s3Key.isEmpty())
        Assertions.assertTrue(!role.isEmpty())
        Assertions.assertTrue(!handler.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun createFunctionTest() = runBlocking {

        functionARN = createNewFunction(functionName, s3BucketName, s3Key, handler, role).toString()
        Assertions.assertTrue(!functionARN.isEmpty())
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun listLambdaTest() = runBlocking {
        listFunctions()
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun getAccountSettings() = runBlocking {
        getSettings()
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun deleteFunctionTest() = runBlocking {
        delLambdaFunction(functionName)
        println("Test 5 passed")
    }

    @Test
    @Order(5)
    fun testLambdaScenario() = runBlocking {
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
        updateFunctionCode(functionNameSc, updatedBucketName, s3Key)

       // println("*** Invoke the function again after updating the code.")
       invokeFunctionSc(functionNameSc)

        // Update the AWS Lambda function configuration.
        println("Update the run time of the function.")
        UpdateFunctionConfiguration(functionNameSc, handler)

        // Delete the AWS Lambda function.
        println("Delete the AWS Lambda function.")
        delFunction(functionNameSc)
    }
}
