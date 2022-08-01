// snippet-sourcedescription:[SubscribeLambda.kt demonstrates how to subscribe to an Amazon Simple Notification Service (Amazon SNS) lambda function.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.SubscribeLambda.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.SubscribeRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.SubscribeLambda.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            <topicArn> <lambdaArn>
        Where:
           topicArn - The ARN of the topic to subscribe.
           lambdaArn - The ARN of an AWS Lambda function.

        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val topicArn = args[0]
    val lambdaArn = args[1]
    subLambda(topicArn, lambdaArn)
}

// snippet-start:[sns.kotlin.SubscribeLambda.main]
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
// snippet-end:[sns.kotlin.SubscribeLambda.main]
