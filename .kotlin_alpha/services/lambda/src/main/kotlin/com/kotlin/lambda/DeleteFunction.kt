// snippet-sourcedescription:[DeleteFunction.kt demonstrates how to delete an AWS Lambda function by using the LambdaClient object.]
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

// snippet-start:[lambda.kotlin.delete.import]
import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.DeleteFunctionRequest
import aws.sdk.kotlin.services.lambda.model.LambdaException
import kotlin.system.exitProcess
// snippet-end:[lambda.kotlin.delete.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
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
    val lambdaClient = LambdaClient { region = "us-west-2" }
    delLambdaFunction(lambdaClient,functionName )
    lambdaClient.close()
}

// snippet-start:[lambda.kotlin.delete.main]
suspend fun delLambdaFunction(awsLambda: LambdaClient, myFunctionName: String) {

        try {
           val functionRequest = DeleteFunctionRequest {
                functionName = myFunctionName
            }

            awsLambda.deleteFunction(functionRequest)
            println("$myFunctionName was deleted")

        } catch (ex: LambdaException) {
            println(ex.message)
            awsLambda.close()
            exitProcess(1)
        }
 }
// snippet-end:[lambda.kotlin.delete.main]
