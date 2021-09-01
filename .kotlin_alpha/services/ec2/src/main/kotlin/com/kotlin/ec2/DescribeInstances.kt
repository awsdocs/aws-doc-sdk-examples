//snippet-sourcedescription:[DescribeInstances.kt demonstrates how to get information about all the Amazon Elastic Compute Cloud (Amazon EC2) Instances associated with an AWS account.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/21/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ec2

// snippet-start:[ec2.kotlin.describe_instances.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeInstancesRequest
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.describe_instances.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val ec2Client = Ec2Client{region = "us-west-2"}
    describeEC2Instances(ec2Client)
}

// snippet-start:[ec2.kotlin.describe_instances.main]
suspend fun describeEC2Instances(ec2: Ec2Client) {

    try {

        val request = DescribeInstancesRequest{
                maxResults = 6
            }

        val response = ec2.describeInstances(request)
            for (reservation in response.reservations!!) {
                for (instance in reservation.instances!!) {
                    println("Instance Id is ${instance.instanceId}")
                    println("Image id is ${instance.imageId}")
                    println("Instance type is ${instance.instanceType}")
                    println("Instance state name is ${instance.state?.name}")
                    println("monitoring information is ${instance.monitoring?.state}")
                }
            }

    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.describe_instances.main]