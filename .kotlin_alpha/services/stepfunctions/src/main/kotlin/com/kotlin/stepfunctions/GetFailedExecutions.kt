//snippet-sourcedescription:[GetExecutionHistory.kt demonstrates how to retrieve the history of the specified execution as a list of events.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Step Functions]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.stepfunctions

// snippet-start:[stepfunctions.kotlin.get_failed_exes.import]
import aws.sdk.kotlin.services.sfn.SfnClient
import aws.sdk.kotlin.services.sfn.model.ExecutionStatus
import aws.sdk.kotlin.services.sfn.model.ListExecutionsRequest
import kotlin.system.exitProcess
// snippet-end:[stepfunctions.kotlin.get_failed_exes.import]

suspend fun main(args:Array<String>){

    val usage = """
      Usage:
         <stateMachineARN> 
    Where:
        stateMachineARN - The ARN of the state machine.
    """

    if (args.size != 1) {
       println(usage)
       exitProcess(0)
     }

    val stateMachineARN = args[0]
    getFailedExes(stateMachineARN)
    }

// snippet-start:[stepfunctions.kotlin.get_failed_exes.main]
suspend fun getFailedExes(stateMachineARN: String?) {

        val executionsRequest = ListExecutionsRequest {
            maxResults= 10
            stateMachineArn = stateMachineARN
            statusFilter = ExecutionStatus.Failed
        }

        SfnClient { region = "us-east-1" }.use { sfnClient ->
            val response = sfnClient.listExecutions(executionsRequest)
            response.executions?.forEach { item ->
                   println("The Amazon Resource Name (ARN) of the failed execution is ${item.executionArn}.")
            }
        }
 }
// snippet-end:[stepfunctions.kotlin.get_failed_exes.main]