//snippet-sourcedescription:[GetTopicAttributes.kt demonstrates how to retrieve the defaults for an Amazon Simple Notification Service (Amazon SNS) topic.]
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

//snippet-start:[sns.kotlin.GetTopicAttributes.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.GetTopicAttributesRequest
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.GetTopicAttributes.import]

suspend fun main(args:Array<String>) {

    val usage = """
    
        Usage: 
            <topicName> 

        Where:
            topicArn - the ARN of the topic.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val topicArn = args[0]
    getSNSTopicAttributes(topicArn)
}

//snippet-start:[sns.kotlin.GetTopicAttributes.main]
suspend fun getSNSTopicAttributes(topicArnVal: String) {

    val request = GetTopicAttributesRequest {
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val result = snsClient.getTopicAttributes(request)
        println("${result.attributes}")
    }
 }
//snippet-end:[sns.kotlin.GetTopicAttributes.main]