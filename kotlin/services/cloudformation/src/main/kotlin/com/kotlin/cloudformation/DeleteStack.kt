// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteStack.kt demonstrates how to delete an existing stack.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudFormation]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudformation

// snippet-start:[cf.kotlin.delete_stack.import]
import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.DeleteStackRequest
import kotlin.system.exitProcess
// snippet-end:[cf.kotlin.delete_stack.import]

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <stackName>

    Where:
        stackName - The name of the AWS CloudFormation stack. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val stackName = args[0]
    deleteSpecificTemplate(stackName)
}

// snippet-start:[cf.kotlin.delete_stack.main]
suspend fun deleteSpecificTemplate(stackNameVal: String?) {

    val request = DeleteStackRequest {
        stackName = stackNameVal
    }

    CloudFormationClient { region = "us-east-1" }.use { cfClient ->
        cfClient.deleteStack(request)
        println("The AWS CloudFormation stack was successfully deleted!")
    }
}
// snippet-end:[cf.kotlin.delete_stack.main]
