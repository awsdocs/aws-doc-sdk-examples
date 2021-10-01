//snippet-sourcedescription:[ListQueues.kt demonstrates how to list Amazon Simple Queue Service (Amazon SQS) queues.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Simple Queue Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/26/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sqs

// snippet-start:[sqs.kotlin.list_queues.import]
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.ListQueuesRequest
import aws.sdk.kotlin.services.sqs.model.SqsException
import kotlin.system.exitProcess
// snippet-end:[sqs.kotlin.list_queues.import]

suspend fun main() {

    val sqsClient = SqsClient { region = "us-east-1" }
    listQueues(sqsClient)
    sqsClient.close()
}

// snippet-start:[sqs.kotlin.list_queues.main]
suspend fun listQueues(sqsClient: SqsClient) {
    println("\nList Queues")

    val prefix = "que"
    try {
        val listQueuesRequest = ListQueuesRequest {
            queueNamePrefix = prefix
        }

        val listQueuesResponse = sqsClient.listQueues(listQueuesRequest)
        for (url in listQueuesResponse.queueUrls!!) {
            println(url)
        }

    } catch (e: SqsException) {
        println(e.message)
        sqsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[sqs.kotlin.list_queues.main]