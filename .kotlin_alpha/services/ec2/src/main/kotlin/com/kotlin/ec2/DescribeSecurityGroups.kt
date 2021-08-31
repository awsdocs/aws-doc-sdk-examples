//snippet-sourcedescription:[DescribeSecurityGroups.kt demonstrates how to get information about all the Amazon Elastic Compute Cloud (Amazon EC2) Security Groups.]
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

// snippet-start:[ec2.kotlin.describe_security_groups.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeSecurityGroupsRequest
import aws.sdk.kotlin.services.ec2.model.SecurityGroup
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.describe_security_groups.import]

suspend fun main(args:Array<String>) {

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
    val ec2Client = Ec2Client{region = "us-east-1"}
    describeEC2SecurityGroups(ec2Client, groupId)
    ec2Client.close()
}

// snippet-start:[ec2.kotlin.describe_security_groups.main]
suspend fun describeEC2SecurityGroups(ec2: Ec2Client, groupId: String) {
    try {
        val request = DescribeSecurityGroupsRequest {
            groupIds = listOf(groupId)
        }
        val response = ec2.describeSecurityGroups(request)
        for (group: SecurityGroup in response.securityGroups!!)
            println("Found Security Group with id ${group.groupId}, vpc id ${group.vpcId} and description ${group.description}")


    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.describe_security_groups.main]