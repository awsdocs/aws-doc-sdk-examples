// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DescribeStacks.kt demonstrates how to obtain information about stacks.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudFormation]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudformation

// snippet-start:[cf.kotlin.get_stacks.import]
import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.DescribeStacksRequest
import aws.sdk.kotlin.services.cloudformation.model.DescribeStacksResponse
// snippet-end:[cf.kotlin.get_stacks.import]

suspend fun main() {
    describeAllStacks()
}

// snippet-start:[cf.kotlin.get_stacks.main]
suspend fun describeAllStacks() {

    CloudFormationClient { region = "us-east-1" }.use { cfClient ->
        val stacksResponse: DescribeStacksResponse = cfClient.describeStacks(DescribeStacksRequest {})
        stacksResponse.stacks?.forEach { stack ->
            println("The stack description is ${stack.description}")
            println("The stack Id is ${stack.stackId}")
        }
    }
}
// snippet-end:[cf.kotlin.get_stacks.main]
