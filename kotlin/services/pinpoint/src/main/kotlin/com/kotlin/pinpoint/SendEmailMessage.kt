// snippet-sourcedescription:[SendEmailMessage.kt demonstrates how to send an email message.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Pinpoint]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pinpoint

// snippet-start:[pinpoint.kotlin.send_email.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.AddressConfiguration
import aws.sdk.kotlin.services.pinpoint.model.ChannelType
import aws.sdk.kotlin.services.pinpoint.model.DirectMessageConfiguration
import aws.sdk.kotlin.services.pinpoint.model.EmailMessage
import aws.sdk.kotlin.services.pinpoint.model.MessageRequest
import aws.sdk.kotlin.services.pinpoint.model.SendMessagesRequest
import aws.sdk.kotlin.services.pinpoint.model.SimpleEmail
import aws.sdk.kotlin.services.pinpoint.model.SimpleEmailPart
import kotlin.system.exitProcess
// snippet-end:[pinpoint.kotlin.send_email.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
        <subject> <appId> <senderAddress> <toAddress>

    Where:
        subject - The email subject to use.
        appId - The Amazon Pinpoint project/application ID to use when you send this message
        senderAddress - The from address. This address has to be verified in Amazon Pinpoint in the region you're using to send email 
        toAddress - The to address. This address has to be verified in Amazon Pinpoint in the region you're using to send email 
    """

    if (args.size != 4) {
        println(usage)
        exitProcess(0)
    }

    val subject = args[0]
    val appId = args[1]
    val senderAddress = args[2]
    val toAddress = args[3]
    sendEmail(subject, appId, senderAddress, toAddress)
}

// snippet-start:[pinpoint.kotlin.send_email.main]
suspend fun sendEmail(
    msgSubject: String?,
    appId: String?,
    senderAddress: String?,
    toAddress: String
) {

    // The body of the email for recipients whose email clients support HTML content.
    val htmlBody = (
        "<h1>Amazon Pinpoint test (AWS SDK for Kotlin)</h1>" +
            "<p>This email was sent through the <a href='https://aws.amazon.com/pinpoint/'>" +
            "Amazon Pinpoint</a> Email API"
        )

    // The character encoding to use for the subject line and the message body.
    val charsetVal = "UTF-8"

    val addressMap = mutableMapOf<String, AddressConfiguration>()
    val configuration = AddressConfiguration {
        channelType = ChannelType.Email
    }

    addressMap[toAddress] = configuration
    val emailPart = SimpleEmailPart {
        data = htmlBody
        charset = charsetVal
    }

    val subjectPartOb = SimpleEmailPart {
        data = msgSubject
        charset = charsetVal
    }

    val simpleEmailOb = SimpleEmail {
        htmlPart = emailPart
        subject = subjectPartOb
    }

    val emailMessageOb = EmailMessage {
        body = htmlBody
        fromAddress = senderAddress
        simpleEmail = simpleEmailOb
    }

    val directMessageConfigurationOb = DirectMessageConfiguration {
        emailMessage = emailMessageOb
    }

    val messageRequestOb = MessageRequest {
        addresses = addressMap
        messageConfiguration = directMessageConfigurationOb
    }

    PinpointClient { region = "us-west-2" }.use { pinpoint ->
        pinpoint.sendMessages(
            SendMessagesRequest {
                applicationId = appId
                messageRequest = messageRequestOb
            }
        )
        println("The email message was successfully sent")
    }
}
// snippet-end:[pinpoint.kotlin.send_email.main]
