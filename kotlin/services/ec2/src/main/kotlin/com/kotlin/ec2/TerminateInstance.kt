// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.kotlin.ec2

// snippet-start:[ec2.kotlin.terminate_instance.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.TerminateInstancesRequest
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.terminate_instance.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <instanceID> 

    Where:
        instanceID - An instance id value that you can obtain from the AWS Management Console. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val instanceID = args[0]
    terminateEC2(instanceID)
}

// snippet-start:[ec2.kotlin.terminate_instance.main]
suspend fun terminateEC2(instanceID: String) {
    val request =
        TerminateInstancesRequest {
            instanceIds = listOf(instanceID)
        }

    Ec2Client { region = "us-west-2" }.use { ec2 ->
        val response = ec2.terminateInstances(request)
        response.terminatingInstances?.forEach { instance ->
            println("The ID of the terminated instance is ${instance.instanceId}")
        }
    }
}
// snippet-end:[ec2.kotlin.terminate_instance.main]
