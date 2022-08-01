// snippet-sourcedescription:[CreateTopic.kt demonstrates how to create an Amazon Simple Notification Service (Amazon SNS) topic.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.CreateTopic.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.CreateTopicRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.CreateTopic.import]

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
            topicName - The name of the topic to create (for example, mytopic).
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val topicName = args[0]
    val topicArn = createSNSTopic(topicName)
    println("The ARN of the new topic is $topicArn")
}

// snippet-start:[sns.kotlin.CreateTopic.main]
suspend fun createSNSTopic(topicName: String): String {

    val request = CreateTopicRequest {
        name = topicName
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val result = snsClient.createTopic(request)
        return result.topicArn.toString()
    }
}
// snippet-end:[sns.kotlin.CreateTopic.main]
