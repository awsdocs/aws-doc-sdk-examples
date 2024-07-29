// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.kotlin.ec2

// snippet-start:[ec2.kotlin.create_security_group.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.AuthorizeSecurityGroupIngressRequest
import aws.sdk.kotlin.services.ec2.model.CreateSecurityGroupRequest
import aws.sdk.kotlin.services.ec2.model.IpPermission
import aws.sdk.kotlin.services.ec2.model.IpRange
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.create_security_group.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <groupName> <groupDesc> <vpcId> 

        Where:
            groupName - A group name (for example, TestKeyPair). 
            groupDesc - A group description  (for example, TestKeyPair). 
            vpc-id - A VPC ID that you can obtain from the AWS Management Console (for example, vpc-xxxxxf2f). 
        """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val groupName = args[0]
    val groupDesc = args[1]
    val vpcId = args[2]
    val id = createEC2SecurityGroup(groupName, groupDesc, vpcId)
    println("Successfully created Security Group with ID $id")
}

// snippet-start:[ec2.kotlin.create_security_group.main]
suspend fun createEC2SecurityGroup(
    groupNameVal: String?,
    groupDescVal: String?,
    vpcIdVal: String?,
): String? {
    val request =
        CreateSecurityGroupRequest {
            groupName = groupNameVal
            description = groupDescVal
            vpcId = vpcIdVal
        }

    Ec2Client { region = "us-west-2" }.use { ec2 ->
        val resp = ec2.createSecurityGroup(request)
        val ipRange =
            IpRange {
                cidrIp = "0.0.0.0/0"
            }

        val ipPerm =
            IpPermission {
                ipProtocol = "tcp"
                toPort = 80
                fromPort = 80
                ipRanges = listOf(ipRange)
            }

        val ipPerm2 =
            IpPermission {
                ipProtocol = "tcp"
                toPort = 22
                fromPort = 22
                ipRanges = listOf(ipRange)
            }

        val authRequest =
            AuthorizeSecurityGroupIngressRequest {
                groupName = groupNameVal
                ipPermissions = listOf(ipPerm, ipPerm2)
            }
        ec2.authorizeSecurityGroupIngress(authRequest)
        println("Successfully added ingress policy to Security Group $groupNameVal")
        return resp.groupId
    }
}
// snippet-end:[ec2.kotlin.create_security_group.main]
