//snippet-sourcedescription:[ListLambda.kt demonstrates how to list your AWS Lambda functions.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Lambda]
//snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lambda

// snippet-start:[lambda.kotlin.list.import]
import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.ListFunctionsRequest
// snippet-end:[lambda.kotlin.list.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    println("List your AWS Lambda functions")
    listFunctions()
   }

// snippet-start:[lambda.kotlin.list.main]
suspend fun listFunctions() {

     val request = ListFunctionsRequest {
         maxItems = 10
     }

      LambdaClient { region = "us-west-2" }.use { awsLambda ->
        val response = awsLambda.listFunctions(request)
        response.functions?.forEach { function ->
             println("The function name is ${function.functionName}")
        }
    }
}
// snippet-end:[lambda.kotlin.list.main]


