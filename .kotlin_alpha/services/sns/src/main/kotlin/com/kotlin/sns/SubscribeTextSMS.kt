//snippet-sourcedescription:[SubscribeTextSMS.kt demonstrates how to subscribe to an Amazon Simple Notification Service (Amazon SNS) text endpoint.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/21/2021]
//snippet-sourceauthor:[scmacdon- AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

//snippet-start:[sns.kotlin.SubscribeTextSMS.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.SubscribeRequest
import aws.sdk.kotlin.services.sns.model.SnsException
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.SubscribeTextSMS.import]

suspend fun main(args:Array<String>) {

    val usage = """
        
        Usage: <topicArn> <phoneNumber>

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
    val snsClient = SnsClient({ region = "us-east-1" })
    subTextSNS(snsClient, topicArn, phoneNumber)
    snsClient.close()
}

//snippet-start:[sns.kotlin.SubscribeTextSMS.main]
suspend fun subTextSNS(snsClient: SnsClient, topicArnVal: String?, phoneNumber: String?) {
    try {

        val request = SubscribeRequest {
            protocol ="sms"
            endpoint = phoneNumber
            returnSubscriptionArn = true
            topicArn = topicArnVal
        }

        val result = snsClient.subscribe(request)
        println("The subscription Arn is ${result.subscriptionArn}")

    } catch (e: SnsException) {
        println(e.message)
        snsClient.close()
        exitProcess(0)
    }
}
//snippet-end:[sns.kotlin.SubscribeTextSMS.main]