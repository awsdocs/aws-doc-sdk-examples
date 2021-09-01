//snippet-sourcedescription:[FindRunningInstances.kt demonstrates how to find running Amazon Elastic Compute Cloud (Amazon EC2) instances by using a filter.]
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

// snippet-start:[ec2.kotlin.running_instances.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeInstancesRequest
import aws.sdk.kotlin.services.ec2.model.Filter
import aws.sdk.kotlin.services.ec2.model.Reservation
import aws.sdk.kotlin.services.ec2.model.Instance
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.running_instances.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val ec2Client = Ec2Client{region = "us-east-1"}
    findRunningEC2Instances(ec2Client)
}

// snippet-start:[ec2.kotlin.running_instances.main]
suspend fun findRunningEC2Instances(ec2: Ec2Client) {
    try {
        val filter = Filter {
                name = "instance-state-name"
                values = listOf("running")
         }

        val request = DescribeInstancesRequest {
                filters = listOf(filter)
         }

        val response = ec2.describeInstances(request)
        for (reservation: Reservation in response.reservations!!) {
            for (instance: Instance in reservation.instances!!)
                 println("Found Reservation with id: ${instance.instanceId}, type: ${instance.instanceType} state: ${instance.state?.name} and monitoring state: ${instance.monitoring?.state}")
       }

    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.running_instances.main]