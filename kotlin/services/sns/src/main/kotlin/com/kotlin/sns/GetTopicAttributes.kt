// snippet-sourcedescription:[GetTopicAttributes.kt demonstrates how to retrieve the defaults for an Amazon Simple Notification Service (Amazon SNS) topic.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.GetTopicAttributes.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.GetTopicAttributesRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.GetTopicAttributes.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    
        Usage: 
            <topicName> 

        Where:
            topicArn - The ARN of the topic.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val topicArn = args[0]
    getSNSTopicAttributes(topicArn)
}

// snippet-start:[sns.kotlin.GetTopicAttributes.main]
suspend fun getSNSTopicAttributes(topicArnVal: String) {

    val request = GetTopicAttributesRequest {
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val result = snsClient.getTopicAttributes(request)
        println("${result.attributes}")
    }
}
// snippet-end:[sns.kotlin.GetTopicAttributes.main]
