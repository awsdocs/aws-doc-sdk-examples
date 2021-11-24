// snippet-sourcedescription:[LambdaInvoke.kt demonstrates how to invoke a Lambda function by using the LambdaClient object.]
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

// snippet-start:[lambda.kotlin.invoke.import]
import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.InvokeRequest
import kotlin.system.exitProcess
// snippet-end:[lambda.kotlin.invoke.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: <functionName> 

        Where:
        functionName - the name of the Lambda function to invoke (for example, myLambda). 
       """
    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val functionName = args[0]
    invokeFunction(functionName)
   }

// snippet-start:[lambda.kotlin.invoke.main]
suspend fun invokeFunction(functionNameVal:String) {

        val json = """{"Hello ":"Paris"}"""
        val byteArray = json.trimIndent().encodeToByteArray()
        val request = InvokeRequest {
            functionName = functionNameVal
            payload = byteArray
        }

        LambdaClient { region = "us-west-2" }.use { awsLambda ->
           val res = awsLambda.invoke(request)
           println(res)
        }
 }
// snippet-end:[lambda.kotlin.invoke.main]