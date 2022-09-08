// snippet-sourcedescription:[ListDeliveryStreams.kt demonstrates how to list all delivery streams.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Kinesis Data Firehose]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.firehose

// snippet-start:[firehose.kotlin.list_streams.import]
import aws.sdk.kotlin.services.firehose.FirehoseClient
import aws.sdk.kotlin.services.firehose.model.ListDeliveryStreamsRequest
// snippet-end:[firehose.kotlin.list_streams.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listStreams()
}

// snippet-start:[firehose.kotlin.list_streams.main]
suspend fun listStreams() {

    FirehoseClient { region = "us-west-2" }.use { firehoseClient ->
        val response = firehoseClient.listDeliveryStreams(ListDeliveryStreamsRequest {})
        response.deliveryStreamNames?.forEach { item ->
            println("The delivery stream name is $item")
        }
    }
}
// snippet-end:[firehose.kotlin.list_streams.main]
