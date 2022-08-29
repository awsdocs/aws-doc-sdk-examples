// snippet-sourcedescription:[DeleteTag.kt demonstrates how to delete tags from an Amazon Simple Notification Service (Amazon SNS) topic.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.delete_tags.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.UntagResourceRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.delete_tags.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            <topicArn> <tagKey>

        Where:
            topicArn - The ARN of the topic to which tags are removed.
            tagKey - The key of the tag to delete.
          
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val topicArn = args[0]
    val tagKey = args[1]
    removeTag(topicArn, tagKey)
}

// snippet-start:[sns.kotlin.delete_tags.main]
suspend fun removeTag(topicArn: String, tagKey: String) {

    val resourceRequest = UntagResourceRequest {
        resourceArn = topicArn
        tagKeys = listOf(tagKey)
    }
    SnsClient { region = "us-east-1" }.use { snsClient ->
        snsClient.untagResource(resourceRequest)
        println("$tagKey was deleted from $topicArn")
    }
}
// snippet-end:[sns.kotlin.delete_tags.main]
