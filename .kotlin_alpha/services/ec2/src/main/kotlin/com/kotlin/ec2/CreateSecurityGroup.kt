//snippet-sourcedescription:[CreateSecurityGroup.kt demonstrates how to create an Amazon Elastic Compute Cloud (Amazon EC2) Security Group.]
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

// snippet-start:[ec2.kotlin.create_security_group.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.CreateSecurityGroupRequest
import aws.sdk.kotlin.services.ec2.model.IpRange
import aws.sdk.kotlin.services.ec2.model.IpPermission
import aws.sdk.kotlin.services.ec2.model.AuthorizeSecurityGroupIngressRequest
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.create_security_group.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <groupName> <groupDesc> <vpcId> 

        Where:
            groupName - a group name (for example, TestKeyPair). 
            groupDesc - a group description  (for example, TestKeyPair). 
            vpc-id - a VPC ID that you can obtain from the AWS Management Console (for example, vpc-xxxxxf2f). 
        """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val groupName = args[0]
    val groupDesc = args[1]
    val vpcId = args[2]
    val ec2Client = Ec2Client{region = "us-west-2"}
    val id = createEC2SecurityGroup(ec2Client, groupName, groupDesc, vpcId)
    println("Successfully created Security Group with ID $id")
    ec2Client.close()
}

// snippet-start:[ec2.kotlin.create_security_group.main]
suspend fun createEC2SecurityGroup(ec2Client: Ec2Client, groupNameVal: String?, groupDescVal: String?, vpcIdVal: String?): String? {
    try {

        val createRequest = CreateSecurityGroupRequest {
            groupName = groupNameVal
            description = groupDescVal
            vpcId = vpcIdVal
        }

        val resp = ec2Client.createSecurityGroup(createRequest)
        val ipRange = IpRange {
            cidrIp = "0.0.0.0/0"
        }

        val ipPerm = IpPermission {
            ipProtocol = "tcp"
            toPort = 80
            fromPort = 80
            ipRanges = listOf(ipRange)
        }

        val ipPerm2 = IpPermission {
            ipProtocol = "tcp"
            toPort = 22
            fromPort = 22
            ipRanges = listOf(ipRange)
        }

        val authRequest = AuthorizeSecurityGroupIngressRequest {
            groupName = groupNameVal
            ipPermissions = listOf(ipPerm, ipPerm2)
        }

        ec2Client.authorizeSecurityGroupIngress(authRequest)
        println("Successfully added ingress policy to Security Group $groupNameVal")
        return resp.groupId

    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.create_security_group.main]