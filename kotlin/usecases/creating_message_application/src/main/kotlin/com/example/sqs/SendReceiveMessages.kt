/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sqs

import aws.sdk.kotlin.services.comprehend.ComprehendClient
import aws.sdk.kotlin.services.comprehend.model.DetectDominantLanguageRequest
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.GetQueueUrlRequest
import aws.sdk.kotlin.services.sqs.model.MessageAttributeValue
import aws.sdk.kotlin.services.sqs.model.PurgeQueueRequest
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import aws.sdk.kotlin.services.sqs.model.SendMessageRequest
import org.springframework.stereotype.Component

@Component
class SendReceiveMessages {
    private val queueNameVal = "Message.fifo"

    // Purges the queue.
    suspend fun purgeMyQueue() {
        var queueUrlVal: String
        val getQueueRequest = GetQueueUrlRequest {
            queueName = queueNameVal
        }
        SqsClient { region = "us-west-2" }.use { sqsClient ->
            queueUrlVal = sqsClient.getQueueUrl(getQueueRequest).queueUrl.toString()
            val queueRequest = PurgeQueueRequest {
                queueUrl = queueUrlVal
            }
            sqsClient.purgeQueue(queueRequest)
        }
    }

    // Retrieves messages from the FIFO queue.
    suspend fun getMessages(): List<MessageData>? {
        val attr: MutableList<String> = ArrayList()
        attr.add("Name")

        val getQueueRequest = GetQueueUrlRequest {
            queueName = queueNameVal
        }

        SqsClient { region = "us-west-2" }.use { sqsClient ->
            val queueUrlVal = sqsClient.getQueueUrl(getQueueRequest).queueUrl

            val receiveRequest = ReceiveMessageRequest {
                queueUrl = queueUrlVal
                maxNumberOfMessages = 10
                waitTimeSeconds = 20
                messageAttributeNames = attr
            }

            val messages = sqsClient.receiveMessage(receiveRequest).messages
            var myMessage: MessageData
            val allMessages = mutableListOf<MessageData>()

            // Push the messages to a list.
            if (messages != null) {
                for (m in messages) {
                    myMessage = MessageData()
                    myMessage.body = m.body
                    myMessage.id = m.messageId
                    val map = m.messageAttributes
                    val `val` = map?.get("Name")
                    if (`val` != null) {
                        myMessage.name = `val`.stringValue
                    }
                    allMessages.add(myMessage)
                }
            }
            return allMessages
        }
    }

    // Adds a new message to the FIFO queue.
    suspend fun processMessage(msg: MessageData) {
        val attributeValue = MessageAttributeValue {
            stringValue = msg.name
            dataType = "String"
        }

        val myMap: MutableMap<String, MessageAttributeValue> = HashMap()
        myMap["Name"] = attributeValue

        val getQueueRequest = GetQueueUrlRequest {
            queueName = queueNameVal
        }

        // Get the language code of the incoming message.
        var lanCode = ""
        val request = DetectDominantLanguageRequest {
            text = msg.body
        }

        ComprehendClient { region = "us-west-2" }.use { comClient ->
            val resp = comClient.detectDominantLanguage(request)
            val allLanList = resp.languages
            if (allLanList != null) {
                for (lang in allLanList) {
                    println("Language is " + lang.languageCode)
                    lanCode = lang.languageCode.toString()
                }
            }
        }

        // Send the message to the FIFO queue.
        SqsClient { region = "us-west-2" }.use { sqsClient ->
            val queueUrlVal: String? = sqsClient.getQueueUrl(getQueueRequest).queueUrl
            val sendMsgRequest = SendMessageRequest {
                queueUrl = queueUrlVal
                messageAttributes = myMap
                messageGroupId = "GroupA_$lanCode"
                messageDeduplicationId = msg.id
                messageBody = msg.body
            }
            sqsClient.sendMessage(sendMsgRequest)
        }
    }
}
