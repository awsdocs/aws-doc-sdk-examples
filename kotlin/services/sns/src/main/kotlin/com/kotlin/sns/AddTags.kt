// snippet-sourcedescription:[AddTags.kt demonstrates how to add tags to an Amazon Simple Notification Service (Amazon SNS) topic.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.add_tags.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.Tag
import aws.sdk.kotlin.services.sns.model.TagResourceRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.add_tags.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            <topicArn>

        Where:
            topicArn - The ARN of the topic to which tags are added.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val topicArn = args[0]
    addTopicTags(topicArn)
}

// snippet-start:[sns.kotlin.add_tags.main]
suspend fun addTopicTags(topicArn: String) {

    val tag = Tag {
        key = "Team"
        value = "Development"
    }

    val tag2 = Tag {
        key = "Environment"
        value = "Gamma"
    }

    val tagList = mutableListOf<Tag>()
    tagList.add(tag)
    tagList.add(tag2)

    val request = TagResourceRequest {
        resourceArn = topicArn
        tags = tagList
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        snsClient.tagResource(request)
        println("Tags have been added to $topicArn")
    }
}
// snippet-end:[sns.kotlin.add_tags.main]
