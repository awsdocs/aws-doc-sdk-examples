// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.pinpoint

// snippet-start:[pinpoint.kotlin.send_email.import]
import aws.sdk.kotlin.services.pinpointemail.PinpointEmailClient
import aws.sdk.kotlin.services.pinpointemail.model.Body
import aws.sdk.kotlin.services.pinpointemail.model.Content
import aws.sdk.kotlin.services.pinpointemail.model.Destination
import aws.sdk.kotlin.services.pinpointemail.model.EmailContent
import aws.sdk.kotlin.services.pinpointemail.model.Message
import aws.sdk.kotlin.services.pinpointemail.model.SendEmailRequest
import kotlin.system.exitProcess
// snippet-end:[pinpoint.kotlin.send_email.import]

// snippet-start:[pinpoint.kotlin.send_email.main]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

val body: String =
    """
    Amazon Pinpoint test (AWS SDK for Kotlin)
            
    This email was sent through the Amazon Pinpoint Email API using the AWS SDK for Kotlin.
                            
    """.trimIndent()

suspend fun main(args: Array<String>) {
    val usage = """
    Usage: 
        <subject> <appId> <senderAddress> <toAddress>

    Where:
        subject - The email subject to use.
        senderAddress - The from address. This address has to be verified in Amazon Pinpoint in the region you're using to send email 
        toAddress - The to address. This address has to be verified in Amazon Pinpoint in the region you're using to send email 
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val subject = args[0]
    val senderAddress = args[1]
    val toAddress = args[2]
    sendEmail(subject, senderAddress, toAddress)
}

suspend fun sendEmail(
    subjectVal: String?,
    senderAddress: String,
    toAddressVal: String,
) {
    var content =
        Content {
            data = body
        }

    val messageBody =
        Body {
            text = content
        }

    val subContent =
        Content {
            data = subjectVal
        }

    val message =
        Message {
            body = messageBody
            subject = subContent
        }

    val destinationOb =
        Destination {
            toAddresses = listOf(toAddressVal)
        }

    val emailContent =
        EmailContent {
            simple = message
        }

    val sendEmailRequest =
        SendEmailRequest {
            fromEmailAddress = senderAddress
            destination = destinationOb
            this.content = emailContent
        }

    PinpointEmailClient { region = "us-east-1" }.use { pinpointemail ->
        pinpointemail.sendEmail(sendEmailRequest)
        println("Message Sent")
    }
}
// snippet-end:[pinpoint.kotlin.send_email.main]
