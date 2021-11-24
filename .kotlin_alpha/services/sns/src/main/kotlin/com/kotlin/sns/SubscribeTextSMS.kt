//snippet-sourcedescription:[SubscribeTextSMS.kt demonstrates how to subscribe to an Amazon Simple Notification Service (Amazon SNS) text endpoint.]
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

//snippet-start:[sns.kotlin.SubscribeTextSMS.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.SubscribeRequest
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.SubscribeTextSMS.import]

suspend fun main(args:Array<String>) {

    val usage = """
        
        Usage: 
            <topicArn> <phoneNumber>

        Where:
            topicArn - the ARN of the topic to publish.
            phoneNumber - a mobile phone number that receives notifications (for example, +1XXX5550100).
            """

     if (args.size != 2) {
         println(usage)
         exitProcess(0)
     }

    val topicArn = args[0]
    val phoneNumber = args[1]
    subTextSNS(topicArn, phoneNumber)
}

//snippet-start:[sns.kotlin.SubscribeTextSMS.main]
suspend fun subTextSNS( topicArnVal: String?, phoneNumber: String?) {

    val request = SubscribeRequest {
        protocol ="sms"
        endpoint = phoneNumber
        returnSubscriptionArn = true
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val result = snsClient.subscribe(request)
        println("The subscription Arn is ${result.subscriptionArn}")
    }
}
//snippet-end:[sns.kotlin.SubscribeTextSMS.main]