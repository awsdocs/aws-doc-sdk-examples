//snippet-sourcedescription:[PutRecord.kt demonstrates how to write a data record into a delivery stream.]
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

// snippet-start:[firehose.kotlin.put_record.import]
import aws.sdk.kotlin.services.firehose.FirehoseClient
import aws.sdk.kotlin.services.firehose.model.PutRecordRequest
import aws.sdk.kotlin.services.firehose.model.Record
import kotlin.system.exitProcess
// snippet-end:[firehose.kotlin.put_record.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <textValue> <streamName> 
        Where:
            textValue - the text used as the data to write to the data stream. 
            streamName - the data stream name. 
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
suspend fun putSingleRecord(textValue: String, streamName: String?) {

        val bytes = textValue.toByteArray()

        val recordOb = Record {
            data = bytes
        }

        val request = PutRecordRequest {
            deliveryStreamName = streamName
            record = recordOb
        }

        FirehoseClient { region = "us-west-2" }.use { firehoseClient ->
          val recordResponse = firehoseClient.putRecord(request)
          println("The record ID is ${recordResponse.recordId}")
       }
}
// snippet-end:[firehose.kotlin.put_record.main]