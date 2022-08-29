// snippet-sourcedescription:[DeleteTopic.kt demonstrates how to delete an Amazon Simple Notification Service (Amazon SNS) topic.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.DeleteTopic.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.DeleteTopicRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.DeleteTopic.import]

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
            topicArn - The ARN of the topic to delete.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val topicArn = args[0]
    deleteSNSTopic(topicArn)
}

// snippet-start:[sns.kotlin.DeleteTopic.main]
suspend fun deleteSNSTopic(topicArnVal: String) {

    val request = DeleteTopicRequest {
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        snsClient.deleteTopic(request)
        println("$topicArnVal was successfully deleted.")
    }
}
// snippet-end:[sns.kotlin.DeleteTopic.main]
