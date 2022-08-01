// snippet-sourcedescription:[ListTopics.kt demonstrates how to get a list of existing Amazon Simple Notification Service (Amazon SNS) topics.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.ListTopics.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.ListTopicsRequest
// snippet-end:[sns.kotlin.ListTopics.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    listSNSTopics()
}

// snippet-start:[sns.kotlin.ListTopics.main]
suspend fun listSNSTopics() {

    SnsClient { region = "us-east-1" }.use { snsClient ->
        val response = snsClient.listTopics(ListTopicsRequest { })
        response.topics?.forEach { topic ->
            println("The topic ARN is ${topic.topicArn}")
        }
    }
}
// snippet-end:[sns.kotlin.ListTopics.main]
