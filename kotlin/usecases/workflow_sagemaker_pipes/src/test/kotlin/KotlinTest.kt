// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.sage.DASHES
import com.example.sage.checkBucket
import com.example.sage.checkFunction
import com.example.sage.checkLambdaRole
import com.example.sage.checkQueue
import com.example.sage.checkSageMakerRole
import com.example.sage.delLambdaFunction
import com.example.sage.deleteBucket
import com.example.sage.deleteEventSourceMapping
import com.example.sage.deleteLambdaRole
import com.example.sage.deletePipeline
import com.example.sage.deleteSQSQueue
import com.example.sage.deleteSagemakerRole
import com.example.sage.executePipeline
import com.example.sage.getOutputResults
import com.example.sage.listBucketObjects
import com.example.sage.putS3Object
import com.example.sage.setupBucket
import com.example.sage.setupPipeline
import com.example.sage.waitForPipelineExecution
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import java.util.Scanner
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class KotlinTest {

    var sageMakerRoleName = ""
    var lambdaRoleName = ""
    var functionKey = ""
    var functionName = ""
    var queueName = ""
    var bucketName = ""
    var bucketFunction = ""
    var lnglatData = ""
    var spatialPipelinePath = ""
    var pipelineName = ""
    var handlerName = "org.example.SageMakerLambdaFunction::handleRequest"

    @BeforeAll
    fun setup() {
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // load the properties file.
        prop.load(input)
        sageMakerRoleName = prop.getProperty("sageMakerRoleName")
        lambdaRoleName = prop.getProperty("lambdaRoleName")
        functionKey = prop.getProperty("functionKey")
        functionName = prop.getProperty("functionName")
        queueName = prop.getProperty("queueName")
        bucketName = prop.getProperty("bucketName")
        bucketFunction = prop.getProperty("bucketFunction")
        lnglatData = prop.getProperty("lnglatData")
        spatialPipelinePath = prop.getProperty("spatialPipelinePath")
        pipelineName = prop.getProperty("pipelineName")
    }

    @Test
    @Order(1)
    fun runWorkflow() = runBlocking {
        println(DASHES)
        println("Welcome to the Amazon SageMaker pipeline example scenario.")
        println(
            """
         This example workflow will guide you through setting up and running an
         Amazon SageMaker pipeline. The pipeline uses an AWS Lambda function and an
         Amazon SQS Queue. It runs a vector enrichment reverse geocode job to
         reverse geocode addresses in an input file and store the results in an export file.
            """.trimIndent(),
        )
        println(DASHES)

        println(DASHES)
        println("First, we will set up the roles, functions, and queue needed by the SageMaker pipeline.")
        val lambdaRoleArn: String = checkLambdaRole(lambdaRoleName)
        val sageMakerRoleArn: String = checkSageMakerRole(sageMakerRoleName)
        val functionArn = checkFunction(functionName, bucketFunction, functionKey, handlerName, lambdaRoleArn)
        val queueUrl = checkQueue(queueName, functionName)
        println(DASHES)

        println(DASHES)
        println("Setting up bucket $bucketName")
        if (!checkBucket(bucketName)) {
            setupBucket(bucketName)
            println("Put $lnglatData into $bucketName")
            val objectKey = "samplefiles/latlongtest.csv"
            putS3Object(bucketName, objectKey, lnglatData)
        }
        println(DASHES)

        println(DASHES)
        println("Now we can create and run our pipeline.")
        setupPipeline(spatialPipelinePath, sageMakerRoleArn, functionArn, pipelineName)
        val pipelineExecutionARN = executePipeline(bucketName, queueUrl, sageMakerRoleArn, pipelineName)
        println("The pipeline execution ARN value is $pipelineExecutionARN")
        waitForPipelineExecution(pipelineExecutionARN)
        println("Wait 30 secs to get output results $bucketName")
        TimeUnit.SECONDS.sleep(30)
        getOutputResults(bucketName)
        println(DASHES)

        println(DASHES)
        println("Do you want to delete the AWS resources used in this Workflow? (y/n)")
        val `in` = Scanner(System.`in`)
        val delResources = `in`.nextLine()
        if (delResources.compareTo("y") == 0) {
            println("Lets clean up the AWS resources. Wait 30 seconds")
            TimeUnit.SECONDS.sleep(30)
            deleteEventSourceMapping(functionName)
            deleteSQSQueue(queueName)
            listBucketObjects(bucketName)
            deleteBucket(bucketName)
            delLambdaFunction(functionName)
            deleteLambdaRole(lambdaRoleName)
            deleteSagemakerRole(sageMakerRoleName)
            deletePipeline(pipelineName)
        } else {
            println("The AWS Resources were not deleted!")
        }
        println(DASHES)

        println(DASHES)
        println("SageMaker pipeline scenario is complete.")
        println(DASHES)

        println("Test 1 passed")
    }
}
