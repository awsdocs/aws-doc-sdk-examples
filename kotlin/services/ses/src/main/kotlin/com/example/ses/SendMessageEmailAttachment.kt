// snippet-sourcedescription:[SendMessageAttachment.kt demonstrates how to send an email message with an attachment by using the Amazon Simple Email Service (Amazon SES).]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Email Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ses

// snippet-start:[ses.kotlin.sendmessageattachment.import]
import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.RawMessage
import aws.sdk.kotlin.services.ses.model.SendRawEmailRequest
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.Properties
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource
import kotlin.system.exitProcess
// snippet-end:[ses.kotlin.sendmessageattachment.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <sender> <recipient> <subject> <fileLocation> 

    Where:
        sender - An email address that represents the sender. 
        recipient -  An email address that represents the recipient. 
        subject - The subject line. 
        fileLocation - The location of a Microsoft Excel file to use as an attachment (C:/AWS/customers.xls). 
    """

    if (args.size != 4) {
        println(usage)
        exitProcess(0)
    }

    val sender = args[0]
    val recipient = args[1]
    val subject = args[2]
    val fileLocation = args[3]

    // The HTML body of the email
    val bodyHTML = (
        "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>" +
            "<p> See the list of customers.</p>" + "</body>" + "</html>"
        )

    try {
        sendemailAttachment(sender, recipient, subject, bodyHTML, bodyHTML, fileLocation)
    } catch (e: MessagingException) {
        e.stackTrace
    }
}

// snippet-start:[ses.kotlin.sendmessageattachment.main]
suspend fun sendemailAttachment(
    sender: String,
    recipient: String,
    subject: String,
    bodyText: String,
    bodyHTML: String,
    fileLocation: String
) {

    val theFile = File(fileLocation)
    val fileContent = Files.readAllBytes(theFile.toPath())
    val session = Session.getDefaultInstance(Properties())

    // Create a new MimeMessage object.
    val message = MimeMessage(session)

    // Add subject, from, and to lines.
    message.setSubject(subject, "UTF-8")
    message.setFrom(InternetAddress(sender))
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient))

    // Create a multipart/alternative child container.
    val msgBody = MimeMultipart("alternative")

    // Create a wrapper for the HTML and text parts.
    val wrap = MimeBodyPart()

    // Define the text part.
    val textPart = MimeBodyPart()
    textPart.setContent(bodyText, "text/plain; charset=UTF-8")

    // Define the HTML part.
    val htmlPart = MimeBodyPart()
    htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8")

    // Add the text and HTML parts to the child container.
    msgBody.addBodyPart(textPart)
    msgBody.addBodyPart(htmlPart)

    // Add the child container to the wrapper object.
    wrap.setContent(msgBody)

    // Create a multipart/mixed parent container.
    val msg = MimeMultipart("mixed")

    // Add the parent container to the message.
    message.setContent(msg)

    // Add the multipart/alternative part to the message.
    msg.addBodyPart(wrap)

    // Define the attachment.
    val att = MimeBodyPart()
    val fds: DataSource =
        ByteArrayDataSource(fileContent, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

    att.dataHandler = DataHandler(fds)

    val reportName = "WorkReport.xls"
    att.fileName = reportName

    // Add the attachment to the message.
    msg.addBodyPart(att)

    println("Attempting to send an email through Amazon SES using the AWS SDK for Kotlin...")
    val outputStream = ByteArrayOutputStream()
    message.writeTo(outputStream)

    val rawMessageOb = RawMessage {
        this.data = outputStream.toByteArray()
    }

    val rawEmailRequest = SendRawEmailRequest {
        rawMessage = rawMessageOb
    }

    SesClient { region = "us-east-1" }.use { sesClient ->
        sesClient.sendRawEmail(rawEmailRequest)
    }
    println("Email sent with attachment")
}
// snippet-end:[ses.kotlin.sendmessageattachment.main]
