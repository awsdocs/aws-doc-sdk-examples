// snippet-sourcedescription:[DeleteStateMachine.kt demonstrates how to delete a state machine for AWS Step Functions.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Step Functions]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.stepfunctions

// snippet-start:[stepfunctions.kotlin.delete_machine.import]
import aws.sdk.kotlin.services.sfn.SfnClient
import aws.sdk.kotlin.services.sfn.model.DeleteStateMachineRequest
import kotlin.system.exitProcess
// snippet-end:[stepfunctions.kotlin.delete_machine.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

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
