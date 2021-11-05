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
import aws.sdk.kotlin.services.sfn.model.SfnException
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
     val sfnClient = SfnClient{region = "us-east-1" }
     deleteMachine(sfnClient,stateMachineARN)
     sfnClient.close()
}

// snippet-start:[stepfunctions.kotlin.delete_machine.main]
suspend fun deleteMachine(sfnClient: SfnClient, stateMachineArnVal: String) {
        try {
            val deleteStateMachineRequest = DeleteStateMachineRequest {
                stateMachineArn = stateMachineArnVal
            }
            sfnClient.deleteStateMachine(deleteStateMachineRequest)
            println("$stateMachineArnVal was successfully deleted.")

        } catch (ex: SfnException) {
            println(ex.message)
            sfnClient.close()
            exitProcess(0)
        }
 }
// snippet-end:[stepfunctions.kotlin.delete_machine.main]