//snippet-sourcedescription:[CreateInstance.kt demonstrates how to create an Amazon Elastic Compute Cloud (Amazon EC2) instance.]
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

// snippet-start:[ec2.kotlin.create_instance.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.RunInstancesRequest
import aws.sdk.kotlin.services.ec2.model.InstanceType
import aws.sdk.kotlin.services.ec2.model.Tag
import aws.sdk.kotlin.services.ec2.model.CreateTagsRequest
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.create_instance.import]

suspend fun main(args:Array<String>) {

    val usage = """

    Usage:
        <name> <amiId>

    Where:
        name - an instance name that you can obtain from the AWS Management Console (for example, ami-xxxxxx5c8b987b1a0). 
        amiId - an Amazon Machine Image (AMI) value that you can obtain from the AWS Management Console (for example, i-xxxxxx2734106d0ab). 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val name = args[0]
    val amiId = args[1]
    val ec2Client = Ec2Client{region = "us-west-2"}
    createEC2Instance(ec2Client, name, amiId)
    ec2Client.close()
}

// snippet-start:[ec2.kotlin.create_instance.main]
suspend fun createEC2Instance(ec2: Ec2Client, name: String?, amiId: String?): String? {

    try {

        val runRequest = RunInstancesRequest {
            imageId = amiId
            instanceType = InstanceType.T1Micro
            maxCount = 1
            minCount = 1
        }

        val response = ec2.runInstances(runRequest)
        val instanceId = response.instances?.get(0)?.instanceId

        val tag = Tag{
            key = "Name"
            value = name
        }

        val tagRequest = CreateTagsRequest {
            resources = listOf(instanceId.toString())
            tags = listOf(tag)
        }

        ec2.createTags(tagRequest)
        println("Successfully started EC2 Instance $instanceId based on AMI $amiId")
        return instanceId

    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.create_instance.main]
