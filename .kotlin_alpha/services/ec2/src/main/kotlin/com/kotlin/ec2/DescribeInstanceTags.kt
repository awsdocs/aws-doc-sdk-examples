//snippet-sourcedescription:[DescribeInstanceTags.kt demonstrates how to describe the specified tags for your Amazon Elastic Compute Cloud (Amazon EC2) resource.]
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

// snippet-start:[ec2.kotlin.describe_instances_tags.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeTagsRequest
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import aws.sdk.kotlin.services.ec2.model.Filter
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.describe_instances_tags.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <resourceIdVal> 

        Where:
            resourceIdVal - the instance ID value that you can obtain from the AWS Management Console (for example, i-xxxxxx0913e05f482).
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val resourceIdVal = args[0]
    val ec2Client = Ec2Client{region = "us-west-2"}
    describeEC2Tags(ec2Client, resourceIdVal)
    ec2Client.close()
}

// snippet-start:[ec2.kotlin.describe_instances_tags.main]
suspend fun describeEC2Tags(ec2: Ec2Client, resourceIdVal: String) {
    try {
        val filter = Filter {
            name = "resource-id"
            values = listOf(resourceIdVal)
        }

        val request  = DescribeTagsRequest {
            filters = listOf(filter)
        }

        val describeTagsResponse =  ec2.describeTags(request)
        val tags = describeTagsResponse.tags

        if (tags != null) {
            for (tag in tags) {
                println("Tag key is ${tag.key}")
                println("Tag value is ${tag.value}")
            }
        }

    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.describe_instances_tags.main]