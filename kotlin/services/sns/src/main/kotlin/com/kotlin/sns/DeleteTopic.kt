//snippet-sourcedescription:[DeleteTopic.kt demonstrates how to delete an Amazon Simple Notification Service (Amazon SNS) topic.]
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

//snippet-start:[sns.kotlin.DeleteTopic.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.DeleteTopicRequest
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.DeleteTopic.import]

suspend fun main(args:Array<String>) {

    val usage = """
    
        Usage: 
            <topicName> 

        Where:
            topicArn - the ARN of the topic to delete.
        """

    if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val topicArn = args[0]
    deleteSNSTopic(topicArn)
}

//snippet-start:[sns.kotlin.DeleteTopic.main]
suspend fun deleteSNSTopic(topicArnVal: String) {

    val request = DeleteTopicRequest {
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
      snsClient.deleteTopic(request)
      println("$topicArnVal was successfully deleted.")
    }
}
//snippet-end:[sns.kotlin.DeleteTopic.main]