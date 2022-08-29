// snippet-sourcedescription:[SubscribeEmail.kt demonstrates how to subscribe to an Amazon Simple Notification Service (Amazon SNS) email endpoint.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.SubscribeEmail.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.SubscribeRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.SubscribeEmail.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            SubscribeEmail  <topicArn> <email>

        Where:
            topicArn - The ARN of the topic to subscribe.
            email - The email address to use.
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val topicArn = args[0]
    val email = args[1]
    val subscriptionArn = subEmail(topicArn, email)
    println("Subscription ARN is $subscriptionArn")
}

// snippet-start:[sns.kotlin.SubscribeEmail.main]
suspend fun subEmail(topicArnVal: String, email: String): String {

    val request = SubscribeRequest {
        protocol = "email"
        endpoint = email
        returnSubscriptionArn = true
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val result = snsClient.subscribe(request)
        return result.subscriptionArn.toString()
    }
}
// snippet-end:[sns.kotlin.SubscribeEmail.main]
