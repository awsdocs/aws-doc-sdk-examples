// snippet-sourcedescription:[DescribeVPCs.kt demonstrates how to get information about all the Amazon Elastic Compute Cloud (Amazon EC2) VPCs.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EC2]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ec2

// snippet-start:[ec2.kotlin.describe_vpc.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeVpcsRequest
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.describe_vpc.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <vpcId> 

    Where:
        vpcId - A VPC ID that you can obtain from the AWS Management Console (for example, vpc-xxxxxf2f).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val vpcId = args[0]
    describeEC2Vpcs(vpcId)
}

// snippet-start:[ec2.kotlin.describe_vpc.main]
suspend fun describeEC2Vpcs(vpcId: String) {

    val request = DescribeVpcsRequest {
        vpcIds = listOf(vpcId)
    }

    Ec2Client { region = "us-west-2" }.use { ec2 ->
        val response = ec2.describeVpcs(request)
        response.vpcs?.forEach { vpc ->
            println("Found VPC with id ${vpc.vpcId} VPC state ${vpc.state} and tenancy ${vpc.instanceTenancy}")
        }
    }
}
// snippet-end:[ec2.kotlin.describe_vpc.main]
