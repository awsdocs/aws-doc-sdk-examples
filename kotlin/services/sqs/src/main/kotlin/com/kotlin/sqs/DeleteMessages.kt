//snippet-sourcedescription:[DeleteMessages.kt demonstrates how to delete Amazon Simple Queue Service (Amazon SQS) messages and a queue.]
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

// snippet-start:[sqs.kotlin.del_messages.import]
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.PurgeQueueRequest
import aws.sdk.kotlin.services.sqs.model.DeleteQueueRequest
import kotlin.system.exitProcess
// snippet-end:[sqs.kotlin.del_messages.import]

suspend fun main(args:Array<String>) {


    val usage = """
        Usage: 
            <queueUrl>
        Where:
           queueUrl - the URL of the queue from which messages are deleted. 
         """

      if (args.size != 1) {
         println(usage)
          exitProcess(0)
     }

    val queueUrl = args[0]
    deleteMessages(queueUrl)
    deleteQueue(queueUrl)
}

// snippet-start:[sqs.kotlin.del_messages.main]
suspend fun deleteMessages(queueUrlVal: String) {
    println("Delete Messages from $queueUrlVal")


    val purgeRequest = PurgeQueueRequest{
       queueUrl = queueUrlVal
    }

    SqsClient { region = "us-east-1" }.use { sqsClient ->
        sqsClient.purgeQueue(purgeRequest)
        println("Messages are successfully deleted from $queueUrlVal")
    }
}


suspend fun deleteQueue(queueUrlVal: String) {

    val request = DeleteQueueRequest{
        queueUrl=queueUrlVal
    }

    SqsClient { region = "us-east-1" }.use { sqsClient ->
        sqsClient.deleteQueue(request)
        println("$queueUrlVal was deleted!")
    }
}
// snippet-end:[sqs.kotlin.del_messages.main]