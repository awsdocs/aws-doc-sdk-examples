// snippet-sourcedescription:[DeleteStream.kt demonstrates how to delete a delivery stream.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Kinesis Data Firehose]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.firehose

// snippet-start:[firehose.kotlin.delete_stream.import]
import aws.sdk.kotlin.services.firehose.FirehoseClient
import aws.sdk.kotlin.services.firehose.model.DeleteDeliveryStreamRequest
import kotlin.system.exitProcess
// snippet-end:[firehose.kotlin.delete_stream.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    
    Usage:
        <streamName> 

    Where:
        streamName - The name of the delivery stream. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val streamName = args[0]
    delStream(streamName)
}

// snippet-start:[firehose.kotlin.delete_stream.main]
suspend fun delStream(streamName: String) {

    val request = DeleteDeliveryStreamRequest {
        deliveryStreamName = streamName
    }

    FirehoseClient { region = "us-west-2" }.use { firehoseClient ->
        firehoseClient.deleteDeliveryStream(request)
        println("Delivery Stream $streamName is deleted")
    }
}
// snippet-end:[firehose.kotlin.delete_stream.main]
