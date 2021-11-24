//snippet-sourcedescription:[Unsubscribe.kt demonstrates how to remove an Amazon Simple Notification Service (Amazon SNS) subscription.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon- AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

//snippet-start:[sns.kotlin.Unsubscribe.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.UnsubscribeRequest
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.Unsubscribe.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage: 
            <subscriptionArn>

        Where:
            subscriptionArn - the ARN of the subscription.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val subArn = args[0]
    unSub(subArn)
}

//snippet-start:[sns.kotlin.Unsubscribe.main]
suspend fun unSub(subscriptionArnVal: String) {

       val request = UnsubscribeRequest {
           subscriptionArn = subscriptionArnVal
        }

       SnsClient { region = "us-east-1" }.use { snsClient ->
         snsClient.unsubscribe(request)
         println("Subscription was removed for ${request.subscriptionArn}")
       }
}
//snippet-end:[sns.kotlin.Unsubscribe.main]