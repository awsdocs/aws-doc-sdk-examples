// snippet-sourcedescription:[SendMessageEmail.kt demonstrates how to send an email message by using the Amazon Simple Email Service (Amazon SES).]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/05/2021]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ses

// snippet-start:[ses.kotlin.sendmessage.import]
import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.Destination
import aws.sdk.kotlin.services.ses.model.Content
import aws.sdk.kotlin.services.ses.model.Body
import aws.sdk.kotlin.services.ses.model.Message
import aws.sdk.kotlin.services.ses.model.SendEmailRequest
import kotlin.system.exitProcess
// snippet-end:[ses.kotlin.sendmessage.import]

suspend fun main(args:Array<String>) {

    val usage = """
    
    Usage:
        <sender> <recipient> <subject> 

    Where:
        sender - an email address that represents the sender. 
        recipient - an email address that represents the recipient. 
        subject - the subject line. 
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val sender = args[0]
    val recipient = args[1]
    val subject = args[2]

    // The HTML body of the email.
    val bodyHTML = ("<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
            + "<p> See the list of customers.</p>" + "</body>" + "</html>")

    send(sender, recipient, subject, bodyHTML)
}

// snippet-start:[ses.kotlin.sendmessage.main]
suspend fun send(
    sender: String?,
    recipient: String,
    subjectVal: String?,
    bodyHTML: String?
) {
    val destinationOb = Destination {
        toAddresses = listOf(recipient)
    }

    val contentOb = Content {
        data = bodyHTML
    }

    val subOb = Content {
         data = subjectVal
    }

    val bodyOb= Body {
        html = contentOb
    }

    val msgOb = Message {
        subject = subOb
        body = bodyOb
    }

    val emailRequest = SendEmailRequest {
        destination = destinationOb
        message = msgOb
        source = sender
       }

    SesClient { region = "us-east-1" }.use { sesClient ->
        println("Attempting to send an email through Amazon SES using the AWS SDK for Kotlin...")
        sesClient.sendEmail(emailRequest)
    }
}
// snippet-end:[ses.kotlin.sendmessage.main]