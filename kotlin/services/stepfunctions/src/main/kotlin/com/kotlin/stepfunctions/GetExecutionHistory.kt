// snippet-sourcedescription:[GetExecutionHistory.kt demonstrates how to retrieve the history of the specified execution as a list of events.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Step Functions]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.stepfunctions

// snippet-start:[stepfunctions.kotlin.get_history.import]
import aws.sdk.kotlin.services.sfn.SfnClient
import aws.sdk.kotlin.services.sfn.model.GetExecutionHistoryRequest
import kotlin.system.exitProcess
// snippet-end:[stepfunctions.kotlin.get_history.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
      Usage:
         <exeARN> 
    Where:
        exeARN - The Amazon Resource Name (ARN) of the execution.

    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val exeARN = args[0]
    val smARN = getExeHistory(exeARN)
    println("The ARN of the new state machine is $smARN")
}

// snippet-start:[stepfunctions.kotlin.get_history.main]
suspend fun getExeHistory(exeARN: String?) {

    val historyRequest = GetExecutionHistoryRequest {
        executionArn = exeARN
        maxResults = 10
    }

    SfnClient { region = "us-east-1" }.use { sfnClient ->
        val response = sfnClient.getExecutionHistory(historyRequest)
        response.events?.forEach { event ->
            println("The event type is ${event.type}")
        }
    }
}
// snippet-end:[stepfunctions.kotlin.get_history.main]
