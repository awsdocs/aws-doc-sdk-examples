// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.ec2

// snippet-start:[ec2.kotlin.running_instances.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeInstancesRequest
import aws.sdk.kotlin.services.ec2.model.Filter
// snippet-end:[ec2.kotlin.running_instances.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    findRunningEC2Instances()
}

// snippet-start:[ec2.kotlin.running_instances.main]
suspend fun findRunningEC2Instances() {
    val filter =
        Filter {
            name = "instance-state-name"
            values = listOf("running")
        }

    val request =
        DescribeInstancesRequest {
            filters = listOf(filter)
        }

    Ec2Client { region = "us-west-2" }.use { ec2 ->
        val response = ec2.describeInstances(request)
        response.reservations?.forEach { reservation ->
            reservation.instances?.forEach { instance ->
                println("Found Reservation with id: ${instance.instanceId}, type: ${instance.instanceType} state: ${instance.state?.name} and monitoring state: ${instance.monitoring?.state}")
            }
        }
    }
}
// snippet-end:[ec2.kotlin.running_instances.main]
