//snippet-sourcedescription:[ListLambda.kt demonstrates how to list your Lambda functions.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Lambda]
//snippet-sourcetype:[full-example]
// snippet-sourcedate:[04/14/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lambda

// snippet-start:[lambda.kotlin.list.import]
import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.FunctionConfiguration
import aws.sdk.kotlin.services.lambda.model.ListFunctionsRequest
import aws.sdk.kotlin.services.lambda.model.LambdaException
import kotlin.system.exitProcess
// snippet-end:[lambda.kotlin.list.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    println("List your AWS Lambda functions")
    val lambdaClient = LambdaClient {region = "us-east-1" }
    listFunctions(lambdaClient)
    lambdaClient.close()
}

// snippet-start:[lambda.kotlin.list.main]
suspend fun listFunctions(lambdaClient: LambdaClient) {

    try {
        val request = ListFunctionsRequest {
            maxItems = 10
        }

        val response = lambdaClient.listFunctions(request)
        val functions = response.functions
        if (functions != null) {
            for (function: FunctionConfiguration in functions) {
                println("The function name is ${function.functionName}")
            }
        }

    } catch (ex: LambdaException) {
        println(ex.message)
        lambdaClient.close()
        exitProcess(1)
    }
}
// snippet-end:[lambda.kotlin.list.main]


