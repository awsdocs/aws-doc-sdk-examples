// snippet-sourcedescription:[SubscribeTextSMS.kt demonstrates how to subscribe to an Amazon Simple Notification Service (Amazon SNS) text endpoint.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.SubscribeTextSMS.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.SubscribeRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.SubscribeTextSMS.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        
        Usage: 
            <topicArn> <phoneNumber>

        Where:
            topicArn - The ARN of the topic to publish.
            phoneNumber - A mobile phone number that receives notifications (for example, +1XXX5550100).
            """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val topicArn = args[0]
    val phoneNumber = args[1]
    subTextSNS(topicArn, phoneNumber)
}

// snippet-start:[sns.kotlin.SubscribeTextSMS.main]
suspend fun subTextSNS(topicArnVal: String?, phoneNumber: String?) {

    val request = SubscribeRequest {
        protocol = "sms"
        endpoint = phoneNumber
        returnSubscriptionArn = true
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val result = snsClient.subscribe(request)
        println("The subscription Arn is ${result.subscriptionArn}")
    }
}
// snippet-end:[sns.kotlin.SubscribeTextSMS.main]
