//snippet-sourcedescription:[CreateQueue.kt demonstrates how to create an Amazon Simple Queue Service (Amazon SQS) queue.]
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

// snippet-start:[sqs.kotlin.create_queue.import]
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.CreateQueueRequest
import aws.sdk.kotlin.services.sqs.model.GetQueueUrlRequest
import kotlin.system.exitProcess
// snippet-end:[sqs.kotlin.create_queue.import]

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
    val queueURL = createQueue(queueName)
    println("The URL: of the new queue is $queueURL")
}

// snippet-start:[sqs.kotlin.create_queue.main]
suspend fun createQueue(queueNameVal: String): String {

    println("Create Queue")
    val createQueueRequest = CreateQueueRequest {
        queueName = queueNameVal
    }

    SqsClient { region = "us-east-1" }.use { sqsClient ->
        sqsClient.createQueue(createQueueRequest)
        println("Get queue url")

        val getQueueUrlRequest = GetQueueUrlRequest {
            queueName = queueNameVal
        }

        val getQueueUrlResponse =  sqsClient.getQueueUrl(getQueueUrlRequest)
        return getQueueUrlResponse.queueUrl.toString()
    }
  }
// snippet-end:[sqs.kotlin.create_queue.main]