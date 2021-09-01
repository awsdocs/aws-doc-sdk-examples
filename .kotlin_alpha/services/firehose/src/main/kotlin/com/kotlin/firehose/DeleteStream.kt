//snippet-sourcedescription:[DeleteStream.kt demonstrates how to delete a delivery stream.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kinesis Data Firehose]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/24/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.firehose

// snippet-start:[firehose.kotlin.delete_stream.import]
import aws.sdk.kotlin.services.firehose.FirehoseClient
import aws.sdk.kotlin.services.firehose.model.DeleteDeliveryStreamRequest
import aws.sdk.kotlin.services.firehose.model.FirehoseException
import kotlin.system.exitProcess
// snippet-end:[firehose.kotlin.delete_stream.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    
    Usage:
        <streamName> 

    Where:
        streamName - the name of the delivery stream. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val streamName = args[0]
    val firehoseClient = FirehoseClient{region="us-east-1"}
    delStream(firehoseClient, streamName)
}

// snippet-start:[firehose.kotlin.delete_stream.main]
suspend fun delStream(firehoseClient: FirehoseClient, streamName: String) {
    try {
        val deleteDeliveryStreamRequest = DeleteDeliveryStreamRequest {
            deliveryStreamName = streamName
        }

        firehoseClient.deleteDeliveryStream(deleteDeliveryStreamRequest)
        println("Delivery Stream $streamName is deleted")

    } catch (ex: FirehoseException) {
        println(ex.message)
        firehoseClient.close()
        exitProcess(0)
    }
}
// snippet-end:[firehose.kotlin.delete_stream.main]