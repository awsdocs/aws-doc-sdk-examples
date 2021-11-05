//snippet-sourcedescription:[SendMessages.kt demonstrates how to send a message to an Amazon Simple Queue Service (Amazon SQS) queue.]
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

// snippet-start:[sqs.kotlin.send_messages.import]
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.SendMessageRequest
import aws.sdk.kotlin.services.sqs.model.SqsException
import aws.sdk.kotlin.services.sqs.model.SendMessageBatchRequestEntry
import aws.sdk.kotlin.services.sqs.model.SendMessageBatchRequest
import kotlin.system.exitProcess
// snippet-end:[sqs.kotlin.send_messages.import]

suspend fun main(args:Array<String>) {


    val usage = """
        Usage: 
            <queueName> <tagName>
        Where:
           queueUrl - the URL of the queue to which messages are sent.
           message - the message to send.

        """

     if (args.size != 2) {
         println(usage)
         exitProcess(0)
     }

    val queueUrl = args[0]
    val message = args[1]
    val sqsClient = SqsClient { region = "us-east-1" }
    sendMessages(sqsClient, queueUrl, message)
    sendBatchMessages(sqsClient, queueUrl)
    println("The multi AWS SQS operation example is complete!")
    sqsClient.close()
}

// snippet-start:[sqs.kotlin.send_messages.main]
suspend fun sendMessages(sqsClient: SqsClient, queueUrlVal: String, message : String) {
    println("Sending multiple messages")
    try {

        println("\nSend message")
        val sendRequest = SendMessageRequest {
            queueUrl = queueUrlVal
            messageBody = message
            delaySeconds = 10
        }

        sqsClient.sendMessage(sendRequest)
        println("A single message was successfully sent.")

    } catch (e: SqsException) {
        println(e.message)
        sqsClient.close()
        exitProcess(0)
    }
}

suspend fun sendBatchMessages(sqsClient: SqsClient, queueUrlVal: String?) {
    println("Sending multiple messages")

    try {
        val msg1 = SendMessageBatchRequestEntry {
            id = "id1"
            messageBody = "Hello from msg 1"
        }

        val msg2 = SendMessageBatchRequestEntry {
            id = "id2"
            messageBody = "Hello from msg 2"
        }

        val sendMessageBatchRequest = SendMessageBatchRequest {
            queueUrl = queueUrlVal
            this.entries = listOf(msg1,msg2)
        }

        sqsClient.sendMessageBatch(sendMessageBatchRequest)
        println("Batch message were successfully sent.")

    } catch (e: SqsException) {
        println(e.message)
        sqsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[sqs.kotlin.send_messages.main]