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

// snippet-start:[stepfunctions.kotlin.get_history.import]
import aws.sdk.kotlin.services.sfn.model.SfnException
import  aws.sdk.kotlin.services.sfn.SfnClient
import aws.sdk.kotlin.services.sfn.model.GetExecutionHistoryRequest
import kotlin.system.exitProcess
// snippet-end:[stepfunctions.kotlin.get_history.import]

suspend fun main(args:Array<String>){

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
    val sfnClient = SfnClient{region = "us-east-1" }
    val smARN = getExeHistory(sfnClient, exeARN)
    println("The ARN of the new state machine is $smARN")
    sfnClient.close()
}

// snippet-start:[stepfunctions.kotlin.get_history.main]
suspend fun getExeHistory(sfnClient: SfnClient, exeARN: String?) {

    try {
        val historyRequest = GetExecutionHistoryRequest {
            executionArn = exeARN
            maxResults = 10
        }

        val response = sfnClient.getExecutionHistory(historyRequest)
        response.events?.forEach { event ->
            println("The event type is ${event.type.toString()}")
            }

    } catch (ex: SfnException) {
        println(ex.message)
        sfnClient.close()
        exitProcess(0)
    }
 }
// snippet-end:[stepfunctions.kotlin.get_history.main]
