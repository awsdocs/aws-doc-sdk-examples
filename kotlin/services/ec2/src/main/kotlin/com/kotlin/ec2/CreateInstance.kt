//snippet-sourcedescription:[CreateInstance.kt demonstrates how to create an Amazon Elastic Compute Cloud (Amazon EC2) instance.]
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

// snippet-start:[ec2.kotlin.create_instance.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.RunInstancesRequest
import aws.sdk.kotlin.services.ec2.model.InstanceType
import aws.sdk.kotlin.services.ec2.model.Tag
import aws.sdk.kotlin.services.ec2.model.CreateTagsRequest
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.create_instance.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

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
    createEC2Instance(name, amiId)
}

// snippet-start:[ec2.kotlin.create_instance.main]
suspend fun createEC2Instance(name: String, amiId: String): String? {

    val request = RunInstancesRequest {
        imageId = amiId
        instanceType = InstanceType.T1Micro
        maxCount = 1
        minCount = 1
    }

    Ec2Client { region = "us-west-2" }.use { ec2 ->
        val response = ec2.runInstances(request)
        val instanceId = response.instances?.get(0)?.instanceId
        val tag = Tag{
            key = "Name"
            value = name
        }

        val requestTags = CreateTagsRequest {
            resources = listOf(instanceId.toString())
            tags = listOf(tag)
        }
        ec2.createTags(requestTags)
          println("Successfully started EC2 Instance $instanceId based on AMI $amiId")
          return instanceId
      }
}
// snippet-end:[ec2.kotlin.create_instance.main]
