// snippet-sourcedescription:[PublishTopic.kt demonstrates how to publish an Amazon Simple Notification Service (Amazon SNS) topic.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.PublishTopic.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.PublishRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.PublishTopic.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
          Usage: 
            <message> <topicArn>

        Where:
            message - The message text to send.
            topicArn - The ARN of the topic to publish.
            """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val message = args[0]
    val topicArn = args[1]
    pubTopic(topicArn, message)
}

// snippet-start:[sns.kotlin.PublishTopic.main]
suspend fun pubTopic(topicArnVal: String, messageVal: String) {

    val request = PublishRequest {
        message = messageVal
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val result = snsClient.publish(request)
        println("${result.messageId} message sent.")
    }
}
// snippet-end:[sns.kotlin.PublishTopic.main]
