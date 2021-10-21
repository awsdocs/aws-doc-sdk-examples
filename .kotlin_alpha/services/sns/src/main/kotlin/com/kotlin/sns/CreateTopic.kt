//snippet-sourcedescription:[CreateTopic.kt demonstrates how to create an Amazon Simple Notification Service (Amazon SNS) topic.]
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

//snippet-start:[sns.kotlin.CreateTopic.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.CreateTopicRequest
import aws.sdk.kotlin.services.sns.model.SnsException
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.CreateTopic.import]

suspend fun main(args:Array<String>) {

    val usage = """
    
        Usage: 
            <topicName> 

        Where:
            topicName - the name of the topic to create (for example, mytopic).
        """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val topicName = args[0]
    val snsClient = SnsClient({ region = "us-east-1" })
    val topicArn = createSNSTopic(snsClient, topicName)
    println("The ARN of the new topic is $topicArn")
    snsClient.close()
}

//snippet-start:[sns.kotlin.CreateTopic.main]
suspend fun createSNSTopic(snsClient: SnsClient, topicName: String): String {

    try {
        val request = CreateTopicRequest {
            name = topicName
        }

        val result = snsClient.createTopic(request)
        return result.topicArn.toString()

    } catch (e: SnsException) {
        println(e.message)
        snsClient.close()
        exitProcess(0)
    }
 }
//snippet-end:[sns.kotlin.CreateTopic.main]