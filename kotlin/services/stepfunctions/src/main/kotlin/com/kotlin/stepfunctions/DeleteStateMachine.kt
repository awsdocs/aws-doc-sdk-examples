//snippet-sourcedescription:[DeleteStateMachine.kt demonstrates how to delete a state machine for AWS Step Functions.]
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

// snippet-start:[stepfunctions.kotlin.delete_machine.import]
import  aws.sdk.kotlin.services.sfn.SfnClient
import aws.sdk.kotlin.services.sfn.model.DeleteStateMachineRequest
import kotlin.system.exitProcess
// snippet-end:[stepfunctions.kotlin.delete_machine.import]

suspend fun main(args:Array<String>){

    val usage = """
      Usage:
         <stateMachineName>
      Where:
         stateMachineArn - The ARN of the state machine to delete.

    """

    if (args.size != 1) {
       println(usage)
       exitProcess(0)
    }

     val stateMachineARN = args[0]
     deleteMachine(stateMachineARN)
}

// snippet-start:[stepfunctions.kotlin.delete_machine.main]
suspend fun deleteMachine(stateMachineArnVal: String) {
        val deleteStateMachineRequest = DeleteStateMachineRequest {
            stateMachineArn = stateMachineArnVal
        }

        SfnClient { region = "us-east-1" }.use { sfnClient ->
            sfnClient.deleteStateMachine(deleteStateMachineRequest)
            println("$stateMachineArnVal was successfully deleted.")
        }
 }
// snippet-end:[stepfunctions.kotlin.delete_machine.main]