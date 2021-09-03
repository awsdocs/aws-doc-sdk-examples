//snippet-sourcedescription:[ListTopics.kt demonstrates how to get a list of existing Amazon Simple Notification Service (Amazon SNS) topics.]
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

//snippet-start:[sns.kotlin.ListTopics.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.ListTopicsRequest
import aws.sdk.kotlin.services.sns.model.SnsException
import aws.sdk.kotlin.services.sns.model.Topic
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.ListTopics.import]

suspend fun main() {

    val snsClient = SnsClient({region = "us-east-1"})
    listSNSTopics(snsClient)
    snsClient.close()
}

//snippet-start:[sns.kotlin.ListTopics.main]
suspend fun listSNSTopics(snsClient: SnsClient) {

    try {

        val result = snsClient.listTopics(ListTopicsRequest { })
        val topics = result.topics
        if (topics != null) {
            for (topic: Topic in topics) {
                println("The topic ARN is ${topic.topicArn}")
            }
        }

    } catch (e: SnsException) {
        println(e.message)
        snsClient.close()
        exitProcess(0)
    }
}
//snippet-end:[sns.kotlin.ListTopics.main]