//snippet-sourcedescription:[RetrieveMessages.kt demonstrates how to retrieve messages from an Amazon Simple Queue Service (Amazon SQS) queue.]
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

// snippet-start:[sqs.kotlin.get_messages.import]
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import aws.sdk.kotlin.services.sqs.model.SqsException
import kotlin.system.exitProcess
// snippet-end:[sqs.kotlin.get_messages.import]

suspend fun main(args:Array<String>) {


    val usage = """
        Usage: 
            <queueName> <tagName>
        Where:
           queueURL - the URL of the queue from which messages are retrieved.
      """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val queueUrl = args[0]
    val sqsClient = SqsClient { region = "us-east-1" }
    receiveMessages(sqsClient, queueUrl)
    println("The AWS SQS operation example is complete!")
    sqsClient.close()
}

// snippet-start:[sqs.kotlin.get_messages.main]
suspend fun receiveMessages(sqsClient: SqsClient, queueUrlVal: String?) {

    println("Retrieving messages from $queueUrlVal")
    try {

        val receiveMessageRequest = ReceiveMessageRequest {
            queueUrl = queueUrlVal
            maxNumberOfMessages =5
        }

        val response =  sqsClient.receiveMessage(receiveMessageRequest)
        val myMessages = response.messages

        if (myMessages != null) {
            for (message in myMessages) {
                println(message.body)
            }
        }

    } catch (e: SqsException) {
        println(e.message)
        sqsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[sqs.kotlin.get_messages.main]