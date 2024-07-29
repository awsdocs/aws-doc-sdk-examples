// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.firehose

// snippet-start:[firehose.kotlin.put_record.import]
import aws.sdk.kotlin.services.firehose.FirehoseClient
import aws.sdk.kotlin.services.firehose.model.PutRecordRequest
import aws.sdk.kotlin.services.firehose.model.Record
import kotlin.system.exitProcess
// snippet-end:[firehose.kotlin.put_record.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <textValue> <streamName> 
        Where:
            textValue - The text used as the data to write to the data stream. 
            streamName - The data stream name. 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val textValue = args[0]
    val streamName = args[1]
    putSingleRecord(textValue, streamName)
}

// snippet-start:[firehose.kotlin.put_record.main]
suspend fun putSingleRecord(
    textValue: String,
    streamName: String?,
) {
    val bytes = textValue.toByteArray()

    val recordOb =
        Record {
            data = bytes
        }

    val request =
        PutRecordRequest {
            deliveryStreamName = streamName
            record = recordOb
        }

    FirehoseClient { region = "us-west-2" }.use { firehoseClient ->
        val recordResponse = firehoseClient.putRecord(request)
        println("The record ID is ${recordResponse.recordId}")
    }
}
// snippet-end:[firehose.kotlin.put_record.main]
