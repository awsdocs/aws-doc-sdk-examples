//snippet-sourcedescription:[ListQueueTags.kt demonstrates how to retrieve tags from an Amazon Simple Queue Service (Amazon SQS) queue.]
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

// snippet-start:[sqs.kotlin.list_tags.import]
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.GetQueueUrlRequest
import aws.sdk.kotlin.services.sqs.model.ListQueueTagsRequest
import kotlin.system.exitProcess
// snippet-end:[sqs.kotlin.list_tags.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage: 
            <queueName>
        Where:
            queueName - the name of the queue.
        """

      if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val queueName = args[0]
    listTags(queueName)
}

// snippet-start:[sqs.kotlin.list_tags.main]
suspend  fun listTags(queueNameVal: String?) {

    val urlRequest = GetQueueUrlRequest {
        queueName = queueNameVal
    }

    SqsClient { region = "us-east-1" }.use { sqsClient ->
        val getQueueUrlResponse = sqsClient.getQueueUrl(urlRequest)
        val queueUrlVal = getQueueUrlResponse.queueUrl
        val listQueueTagsRequest = ListQueueTagsRequest {
            queueUrl = queueUrlVal
        }

        val listQueueTagsResponse = sqsClient.listQueueTags(listQueueTagsRequest)
        println("ListQueueTags: tags for queue $queueNameVal are ${listQueueTagsResponse.tags}")
    }
}
// snippet-end:[sqs.kotlin.list_tags.main]