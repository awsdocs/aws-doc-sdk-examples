//snippet-sourcedescription:[ListTags.kt demonstrates how to retrieve tags from an Amazon Simple Notification Service (Amazon SNS) topic.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/21/2021]
//snippet-sourceauthor:[scmacdon- AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

//snippet-start:[sns.kotlin.list_tags.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.ListTagsForResourceRequest
import aws.sdk.kotlin.services.sns.model.SnsException
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.list_tags.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage: <topicArn>
    
        Where:
            topicArn - the ARN of the topic from which tags are listed.
        """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val topicArn = args[0]
    val snsClient = SnsClient{ region = "us-east-1" }
    listTopicTags(snsClient, topicArn)
    snsClient.close()

}
//snippet-start:[sns.kotlin.list_tags.main]
suspend fun listTopicTags(snsClient: SnsClient, topicArn: String?) {

    try {
        val tagsForResourceRequest = ListTagsForResourceRequest {
            resourceArn = topicArn
        }

        val response = snsClient.listTagsForResource(tagsForResourceRequest)
        println("Tags for topic $topicArn are "+response.tags)

    } catch (e: SnsException) {
        println(e.message)
        snsClient.close()
        exitProcess(0)
    }
}
//snippet-end:[sns.kotlin.list_tags.main]