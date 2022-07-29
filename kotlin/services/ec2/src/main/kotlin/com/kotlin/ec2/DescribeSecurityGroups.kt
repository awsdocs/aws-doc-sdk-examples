// snippet-sourcedescription:[DescribeSecurityGroups.kt demonstrates how to get information about all the Amazon Elastic Compute Cloud (Amazon EC2) Security Groups.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EC2]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ec2

// snippet-start:[ec2.kotlin.describe_security_groups.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeSecurityGroupsRequest
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.describe_security_groups.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <groupId> 

    Where:
        groupId - a security group id. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val groupId = args[0]
    describeEC2SecurityGroups(groupId)
}

// snippet-start:[ec2.kotlin.describe_security_groups.main]
suspend fun describeEC2SecurityGroups(groupId: String) {

    val request = DescribeSecurityGroupsRequest {
        groupIds = listOf(groupId)
    }

    Ec2Client { region = "us-west-2" }.use { ec2 ->

        val response = ec2.describeSecurityGroups(request)
        response.securityGroups?.forEach { group ->
            println("Found Security Group with id ${group.groupId}, vpc id ${group.vpcId} and description ${group.description}")
        }
    }
}
// snippet-end:[ec2.kotlin.describe_security_groups.main]
