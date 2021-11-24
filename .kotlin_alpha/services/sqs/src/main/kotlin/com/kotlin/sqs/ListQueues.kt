//snippet-sourcedescription:[ListQueues.kt demonstrates how to list Amazon Simple Queue Service (Amazon SQS) queues.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Simple Queue Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sqs

// snippet-start:[sqs.kotlin.list_queues.import]
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.ListQueuesRequest
// snippet-end:[sqs.kotlin.list_queues.import]

suspend fun main() {
    listQueues()
}

// snippet-start:[sqs.kotlin.list_queues.main]
suspend fun listQueues() {
    println("\nList Queues")

    val prefix = "que"
    val listQueuesRequest = ListQueuesRequest {
        queueNamePrefix = prefix
    }

    SqsClient { region = "us-east-1" }.use { sqsClient ->
      val response = sqsClient.listQueues(listQueuesRequest)
        response.queueUrls?.forEach { url ->
             println(url)
        }
    }
}
// snippet-end:[sqs.kotlin.list_queues.main]