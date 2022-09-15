// snippet-sourcedescription:[PublishTextSMS.kt demonstrates how to send an Amazon Simple Notification Service (Amazon SNS) text message.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.PublishTextSMS.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.PublishRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.PublishTextSMS.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    
        Usage: 
            <message> <phoneNumber>

        Where:
            message - The message text to send.
            phoneNumber - The mobile phone number to which a message is sent (for example, +1XXX5550100). 
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val message = args[0]
    val phoneNumber = args[1]
    pubTextSMS(message, phoneNumber)
}

// snippet-start:[sns.kotlin.PublishTextSMS.main]
suspend fun pubTextSMS(messageVal: String?, phoneNumberVal: String?) {

    val request = PublishRequest {
        message = messageVal
        phoneNumber = phoneNumberVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val result = snsClient.publish(request)
        println("${result.messageId} message sent.")
    }
}
// snippet-end:[sns.kotlin.PublishTextSMS.main]
