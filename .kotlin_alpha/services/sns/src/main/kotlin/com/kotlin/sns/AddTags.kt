//snippet-sourcedescription:[AddTags.kt demonstrates how to add tags to an Amazon Simple Notification Service (Amazon SNS) topic.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon- AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

//snippet-start:[sns.kotlin.add_tags.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.Tag
import aws.sdk.kotlin.services.sns.model.TagResourceRequest
import aws.sdk.kotlin.services.sns.model.SnsException
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.add_tags.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage: 
         <topicArn>

        Where:
            topicArn - the ARN of the topic to which tags are added.
        """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val topicArn = args[0]
    val snsClient = SnsClient{ region = "us-east-1" }
    addTopicTags(snsClient, topicArn)
    snsClient.close()
}

//snippet-start:[sns.kotlin.add_tags.main]
suspend fun addTopicTags(snsClient: SnsClient, topicArn: String) {

    try {
        val tag = Tag {
            key ="Team"
            value = "Development"
        }

        val tag2 = Tag {
            key = "Environment"
            value = "Gamma"
        }

        val tagList  = mutableListOf<Tag>()
        tagList.add(tag)
        tagList.add(tag2)

        val tagResourceRequest = TagResourceRequest {
            resourceArn=topicArn
            tags = tagList
        }

        snsClient.tagResource(tagResourceRequest)
        println("Tags have been added to $topicArn")

    } catch (e: SnsException) {
        println(e.message)
        snsClient.close()
        exitProcess(0)
    }
}
//snippet-end:[sns.kotlin.add_tags.main]