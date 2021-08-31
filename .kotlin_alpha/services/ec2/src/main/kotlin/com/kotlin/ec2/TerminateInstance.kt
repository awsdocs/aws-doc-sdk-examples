//snippet-sourcedescription:[TerminateInstance.kt demonstrates how to terminate an Amazon Elastic Compute Cloud (Amazon EC2) instance.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/22/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.ec2

// snippet-start:[ec2.kotlin.terminate_instance.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.TerminateInstancesRequest
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.terminate_instance.import]

suspend fun main(args:Array<String>) {

    val usage = """

    Usage:
        <instanceID> 

    Where:
        instanceId - an instance id value that you can obtain from the AWS Management Console. 
         
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val instanceId = args[0];
    val ec2Client = Ec2Client{region = "us-west-2"}
    terminateEC2(ec2Client, instanceId)
}

// snippet-start:[ec2.kotlin.terminate_instance.main]
suspend fun terminateEC2(ec2: Ec2Client, instanceID: String) {
    try {
        val ti  = TerminateInstancesRequest {
            instanceIds = listOf(instanceID)
        }

        val response = ec2.terminateInstances(ti)
        val list = response.terminatingInstances!!
        for (i in list.indices) {
            val sc = list[i]
            println("The ID of the terminated instance is ${sc.instanceId}")
        }
    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.terminate_instance.main]