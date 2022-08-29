// snippet-sourcedescription:[DeleteFunction.kt demonstrates how to delete an AWS Lambda function by using the LambdaClient object.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Lambda]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lambda

// snippet-start:[lambda.kotlin.delete.import]
import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.DeleteFunctionRequest
import kotlin.system.exitProcess
// snippet-end:[lambda.kotlin.delete.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
        <functionName>  

    Where:
        functionName - the name of the Lambda function. 
   """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val functionName = args[0]
    delLambdaFunction(functionName)
}

// snippet-start:[lambda.kotlin.delete.main]
suspend fun delLambdaFunction(myFunctionName: String) {

    val request = DeleteFunctionRequest {
        functionName = myFunctionName
    }

    LambdaClient { region = "us-west-2" }.use { awsLambda ->
        awsLambda.deleteFunction(request)
        println("$myFunctionName was deleted")
    }
}
// snippet-end:[lambda.kotlin.delete.main]
