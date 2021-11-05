//snippet-sourcedescription:[SendEmailMessage.kt demonstrates how to send an email message.]
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

//snippet-start:[pinpoint.kotlin.send_email.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.SendMessagesRequest
import aws.sdk.kotlin.services.pinpoint.model.AddressConfiguration
import aws.sdk.kotlin.services.pinpoint.model.ChannelType
import aws.sdk.kotlin.services.pinpoint.model.SimpleEmailPart
import aws.sdk.kotlin.services.pinpoint.model.SimpleEmail
import aws.sdk.kotlin.services.pinpoint.model.EmailMessage
import aws.sdk.kotlin.services.pinpoint.model.DirectMessageConfiguration
import aws.sdk.kotlin.services.pinpoint.model.MessageRequest
import aws.sdk.kotlin.services.pinpoint.model.PinpointException
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.send_email.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
        <subject> <appId> <senderAddress> <toAddress>

    Where:
        subject - the email subject to use.
        appId - the Amazon Pinpoint project/application ID to use when you send this message
        senderAddress - the from address. This address has to be verified in Amazon Pinpoint in the region you're using to send email 
        toAddress - the to address. This address has to be verified in Amazon Pinpoint in the region you're using to send email 
    """

     if (args.size != 4) {
         println(usage)
         exitProcess(0)
     }

    val subject = args[0]
    val appId = args[1]
    val senderAddress = args[2]
    val toAddress = args[3]

    val pinpointClient = PinpointClient { region = "us-east-1" }
    sendEmail(pinpointClient, subject, appId, senderAddress, toAddress)
    pinpointClient.close()
}

//snippet-start:[pinpoint.kotlin.send_email.main]
    suspend fun sendEmail(
        pinpoint: PinpointClient,
        msgSubject: String?,
        appId: String?,
        senderAddress: String?,
        toAddress: String) {

        // The email body for recipients with non-HTML email clients.
        val textBody = """
        Amazon Pinpoint Test (SDK for Kotlin)
        ---------------------------------
        This email was sent using the Amazon Pinpoint Kotlin API.
        """.trimIndent()

        // The body of the email for recipients whose email clients support HTML content.
        val htmlBody = ("<h1>Amazon Pinpoint test (AWS SDK for Kotlin)</h1>"
                + "<p>This email was sent through the <a href='https://aws.amazon.com/pinpoint/'>"
                + "Amazon Pinpoint</a> Email API")

        // The character encoding to use for the subject line and the message body.
        var charset = "UTF-8"

        try {

            val addressMap = mutableMapOf<String, AddressConfiguration>()
            val configuration = AddressConfiguration {
                channelType = ChannelType.Email
            }

            addressMap[toAddress] = configuration
            val emailPart = SimpleEmailPart {
                data=htmlBody
                charset=charset
            }

            val subjectPartOb = SimpleEmailPart {
                data = msgSubject
                charset = charset
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

            val messagesRequestOb = SendMessagesRequest{
                applicationId = appId
                messageRequest = messageRequestOb
            }

            pinpoint.sendMessages(messagesRequestOb)
            println("The email message was successfully sent")

        } catch (ex: PinpointException) {
            println(ex.message)
            pinpoint.close()
            exitProcess(0)
        }
 }
//snippet-end:[pinpoint.kotlin.send_email.main]