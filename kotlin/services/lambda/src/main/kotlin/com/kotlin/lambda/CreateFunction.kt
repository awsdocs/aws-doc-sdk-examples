// snippet-sourcedescription:[CreateFunction.kt demonstrates how to create an AWS Lambda function by using the LambdaClient object.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/04/2021]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lambda

// snippet-start:[lambda.kotlin.create.import]
import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.CreateFunctionRequest
import aws.sdk.kotlin.services.lambda.model.FunctionCode
import aws.sdk.kotlin.services.lambda.model.Runtime
import kotlin.system.exitProcess
// snippet-end:[lambda.kotlin.create.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
        <functionName> <s3BucketName> <s3Key> <role> <handler> 

    Where:
        functionName - the name of the Lambda function. 
        s3BucketName - the Amazon Simple Storage Service (Amazon S3) bucket name that stores the JAR file for the Lambda function. 
        s3Key - the key name of the JAR file.
        role - the role ARN that has Lambda permissions. 
        handler - the fully qualifed method name (for example, example.Handler::handleRequest).  
    """

    if (args.size != 5) {
        println(usage)
        exitProcess(0)
    }

    val functionName = args[0]
    val s3BucketName = args[1]
    val s3Key = args[2]
    val role = args[3]
    val handler = args[4]

    val functionArn = createNewFunction(functionName, s3BucketName, s3Key, handler, role)
    println("The function ARN is $functionArn")
}

// snippet-start:[lambda.kotlin.create.main]
suspend fun createNewFunction (
         myFunctionName: String,
        s3BucketName: String,
        myS3Key: String,
        myHandler: String,
        myRole: String
    ): String? {

        val functionCode = FunctionCode {
                s3Bucket = s3BucketName
                s3Key = myS3Key
            }

        val request = CreateFunctionRequest {
            functionName = myFunctionName
            code = functionCode
            description = "Created by the Lambda Kotlin API"
            handler = myHandler
            role = myRole
            runtime = Runtime.Java8
        }

        LambdaClient { region = "us-west-2" }.use { awsLambda ->
            val functionResponse = awsLambda.createFunction(request)
            return functionResponse.functionArn
        }
    }
// snippet-end:[lambda.kotlin.create.main]