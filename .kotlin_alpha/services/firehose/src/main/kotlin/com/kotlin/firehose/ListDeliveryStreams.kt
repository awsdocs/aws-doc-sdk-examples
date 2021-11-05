//snippet-sourcedescription:[ListDeliveryStreams.kt demonstrates how to list all delivery streams.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kinesis Data Firehose]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.firehose

// snippet-start:[firehose.kotlin.list_streams.import]
import aws.sdk.kotlin.services.firehose.FirehoseClient
import aws.sdk.kotlin.services.firehose.model.FirehoseException
import aws.sdk.kotlin.services.firehose.model.ListDeliveryStreamsRequest
import kotlin.system.exitProcess
// snippet-end:[firehose.kotlin.list_streams.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val firehoseClient = FirehoseClient{region="us-east-1"}
    listStreams(firehoseClient)
}

// snippet-start:[firehose.kotlin.list_streams.main]
suspend fun listStreams(firehoseClient: FirehoseClient) {
    try {
        val response = firehoseClient.listDeliveryStreams(ListDeliveryStreamsRequest{})
        response.deliveryStreamNames?.forEach { item ->
            println("The delivery stream name is $item")
        }

    } catch (ex: FirehoseException) {
        println(ex.message)
        firehoseClient.close()
        exitProcess(0)
    }
}
// snippet-end:[firehose.kotlin.list_streams.main]