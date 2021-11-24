//snippet-sourcedescription:[ListStateMachines.kt demonstrates how to list existing state machines for AWS Step Functions.]
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

// snippet-start:[stepfunctions.kotlin.list_machines.import]
import  aws.sdk.kotlin.services.sfn.SfnClient
import aws.sdk.kotlin.services.sfn.model.ListStateMachinesRequest
// snippet-end:[stepfunctions.kotlin.list_machines.import]

suspend fun main() {
    listMachines()
}

// snippet-start:[stepfunctions.kotlin.list_machines.main]
suspend fun listMachines() {

       SfnClient { region = "us-east-1" }.use { sfnClient ->
         val response = sfnClient.listStateMachines(ListStateMachinesRequest{})
          response.stateMachines?.forEach { machine ->
                    println("The name of the state machine is ${machine.name}")
                    println("The ARN value is ${machine.stateMachineArn}")
                }
        }
 }
// snippet-end:[stepfunctions.kotlin.list_machines.main]