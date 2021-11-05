//snippet-sourcedescription:[DescribeAddresses.kt demonstrates how to get information about elastic IP addresses.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ec2

// snippet-start:[ec2.kotlin.describe_addresses.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeAddressesRequest
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.describe_addresses.import]


/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val ec2Client = Ec2Client{region = "us-east-1"}
    describeEC2Address(ec2Client)
}

// snippet-start:[ec2.kotlin.describe_addresses.main]
suspend fun describeEC2Address(ec2: Ec2Client) {
    try {
        val response = ec2.describeAddresses(DescribeAddressesRequest{})
        response.addresses?.forEach { address ->
            println("Found address with public IP ${address.publicIp}, domain is ${address.domain}, allocation id ${address.allocationId} and NIC id: ${address.networkInterfaceId} ")
        }

    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.describe_addresses.main]