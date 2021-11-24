//snippet-sourcedescription:[ListSubscriptions.kt demonstrates how to list existing Amazon Simple Notification Service (Amazon SNS) subscriptions.]
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

//snippet-start:[sns.kotlin.ListSubscriptions.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.ListSubscriptionsRequest
//snippet-end:[sns.kotlin.ListSubscriptions.import]


suspend fun main() {
    listSNSSubscriptions()
}

//snippet-start:[sns.kotlin.ListSubscriptions.main]
suspend fun listSNSSubscriptions() {

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val response = snsClient.listSubscriptions(ListSubscriptionsRequest{})
        response.subscriptions?.forEach { sub ->
            println("Sub ARN is ${sub.subscriptionArn}")
            println("Sub protocol is ${sub.protocol}")
        }
    }
}
//snippet-end:[sns.kotlin.ListSubscriptions.main]