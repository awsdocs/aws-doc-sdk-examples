//snippet-sourcedescription:[SubscribeLambda.kt demonstrates how to subscribe to an Amazon Simple Notification Service (Amazon SNS) lambda function.]
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

//snippet-start:[sns.kotlin.SubscribeLambda.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.SubscribeRequest
import kotlin.system.exitProcess
//snippet-end:[sns.kotlin.SubscribeLambda.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage: 
            <topicArn> <lambdaArn>
        Where:
           topicArn - the ARN of the topic to subscribe.
           lambdaArn - the ARN of an AWS Lambda function.

        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
     }

    val topicArn = args[0]
    val lambdaArn = args[1]
    subLambda(topicArn, lambdaArn)
    }

//snippet-start:[sns.kotlin.SubscribeLambda.main]
suspend fun subLambda(topicArnVal: String?, lambdaArn: String?) {

    val request = SubscribeRequest {
        protocol = "lambda"
        endpoint = lambdaArn
        returnSubscriptionArn = true
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val result = snsClient.subscribe(request)
        println(" The subscription Arn is ${result.subscriptionArn}")
    }
}
//snippet-end:[sns.kotlin.SubscribeLambda.main]