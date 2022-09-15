// snippet-sourcedescription:[Unsubscribe.kt demonstrates how to remove an Amazon Simple Notification Service (Amazon SNS) subscription.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.Unsubscribe.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.UnsubscribeRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.Unsubscribe.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            <subscriptionArn>

        Where:
            subscriptionArn - The ARN of the subscription.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val subArn = args[0]
    unSub(subArn)
}

// snippet-start:[sns.kotlin.Unsubscribe.main]
suspend fun unSub(subscriptionArnVal: String) {

    val request = UnsubscribeRequest {
        subscriptionArn = subscriptionArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        snsClient.unsubscribe(request)
        println("Subscription was removed for ${request.subscriptionArn}")
    }
}
// snippet-end:[sns.kotlin.Unsubscribe.main]
