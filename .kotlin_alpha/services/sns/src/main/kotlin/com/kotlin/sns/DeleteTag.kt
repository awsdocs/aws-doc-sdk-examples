//snippet-sourcedescription:[DeleteTag.kt demonstrates how to delete tags from an Amazon Simple Notification Service (Amazon SNS) topic.]
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

//snippet-start:[sns.kotlin.delete_tags.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.UntagResourceRequest
import aws.sdk.kotlin.services.sns.model.SnsException
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.delete_tags.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage: 
         <topicArn> <tagKey>

        Where:
            topicArn - the ARN of the topic to which tags are removed.
            tagKey - the key of the tag to delete.
          
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
     }

    val topicArn = args[0]
    val tagKey = args[1]
    val snsClient = SnsClient{ region = "us-east-1" }
    removeTag(snsClient, topicArn, tagKey)
    snsClient.close()
}

//snippet-start:[sns.kotlin.delete_tags.main]
suspend fun removeTag(snsClient: SnsClient, topicArn: String, tagKey: String) {
    try {

        val resourceRequest = UntagResourceRequest {
            resourceArn = topicArn
            tagKeys = listOf(tagKey)
        }

        snsClient.untagResource(resourceRequest)
        println("$tagKey was deleted from $topicArn")

    } catch (e: SnsException) {
        println(e.message)
        snsClient.close()
        exitProcess(0)
    }
}
//snippet-end:[sns.kotlin.delete_tags.main]