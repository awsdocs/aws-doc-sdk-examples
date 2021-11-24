//snippet-sourcedescription:[SendMessage.kt demonstrates how to send an SMS message using Amazon Pinpoint.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pinpoint

//snippet-start:[pinpoint.kotlin.sendmsg.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.DirectMessageConfiguration
import aws.sdk.kotlin.services.pinpoint.model.AddressConfiguration
import aws.sdk.kotlin.services.pinpoint.model.ChannelType
import aws.sdk.kotlin.services.pinpoint.model.SmsMessage
import aws.sdk.kotlin.services.pinpoint.model.MessageType
import aws.sdk.kotlin.services.pinpoint.model.MessageRequest
import aws.sdk.kotlin.services.pinpoint.model.SendMessagesRequest
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.sendmsg.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: <message> <appId> <originationNumber> <destinationNumber>

    Where:
        message - the body of the message to send.
        appId - the Amazon Pinpoint project/application ID to use when you send this message.
        originationNumber - the phone number or short code that you specify has to be associated with your Amazon Pinpoint account. For best results, specify long codes in E.164 format (for example, +1-555-555-5654). 
        destinationNumber - the recipient's phone number.  For best results, you should specify the phone number in E.164 format (for example, +1-555-555-5654).
    """

     if (args.size != 4) {
         println(usage)
         exitProcess(0)
     }

    val message = args[0]
    val appId = args[1]
    val originationNumber = args[2]
    val destinationNumber = args[3]
    println("Sending a message")
    sendSMSMessage(message, appId, originationNumber, destinationNumber)
 }

//snippet-start:[pinpoint.kotlin.sendmsg.main]
 suspend fun sendSMSMessage(
        message: String,
        appId: String,
        originationNumberVal: String,
        destinationNumberVal: String
    ) {

     // The type of SMS message that you want to send. If you plan to send
     // time-sensitive content, specify TRANSACTIONAL. If you plan to send
     // marketing-related content, specify PROMOTIONAL.
     val messageTypeVal = "TRANSACTIONAL"

     // The registered keyword associated with the originating short code.
     val registeredKeyword = "myKeyword"

     // The sender ID to use when sending the message. Support for sender ID
     // varies by country or region. For more information, see
     // https://docs.aws.amazon.com/pinpoint/latest/userguide/channels-sms-countries.html
     val senderIdVal = "MySenderID"

     val addressMap = mutableMapOf<String, AddressConfiguration>()
     val addConfig = AddressConfiguration {
         channelType = ChannelType.Sms
      }
     addressMap[destinationNumberVal] = addConfig

     val smsMessageOb = SmsMessage {
         body = message
         messageType = MessageType.fromValue(messageTypeVal)
         originationNumber = originationNumberVal
         senderId = senderIdVal
         keyword = registeredKeyword
     }

     val directOb = DirectMessageConfiguration {
         smsMessage = smsMessageOb
     }

     val msgReq = MessageRequest{
         addresses = addressMap
         messageConfiguration = directOb
     }

     PinpointClient { region = "us-west-2" }.use { pinpoint ->
        pinpoint.sendMessages(SendMessagesRequest {
            applicationId = appId
            messageRequest = msgReq
        })
        println("The SMS message was successfully sent to $destinationNumberVal")
       }
    }
//snippet-end:[pinpoint.kotlin.sendmsg.main]