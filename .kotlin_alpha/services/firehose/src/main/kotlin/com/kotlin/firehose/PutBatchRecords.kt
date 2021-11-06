//snippet-sourcedescription:[PutBatchRecords.kt demonstrates how to write multiple data records into a delivery stream and check each record using the response object.]
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

// snippet-start:[firehose.kotlin.put_batch_records.import]
import aws.sdk.kotlin.services.firehose.FirehoseClient
import aws.sdk.kotlin.services.firehose.model.FirehoseException
import aws.sdk.kotlin.services.firehose.model.PutRecordBatchRequest
import aws.sdk.kotlin.services.firehose.model.Record
import com.example.firehose.StockTradeGenerator
import kotlinx.coroutines.delay
import java.util.ArrayList
import kotlin.system.exitProcess
// snippet-end:[firehose.kotlin.put_batch_records.import]

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
            streamName - the data stream name. 
    """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val streamName = args[0]
    val firehoseClient = FirehoseClient{region="us-east-1"}
    addStockTradeData(firehoseClient, streamName)
}

// snippet-start:[firehose.kotlin.put_batch_records.main]
suspend fun addStockTradeData(firehoseClient: FirehoseClient, streamName: String?) {
    val recordList: MutableList<Record> = ArrayList()
    try {

        // Repeatedly send stock trades with a 100 milliseconds wait in between.
        val stockTradeGenerator = StockTradeGenerator()
        val index = 100

        // Populate the list with StockTrade data.
        for (x in 0 until index) {
            val trade = stockTradeGenerator.randomTrade
            val bytes = trade.toJsonAsBytes()
            val myRecord = Record {
                 data = bytes
            }

            println("Adding trade: $trade")
            recordList.add(myRecord)
            delay(100)
        }
        val recordBatchRequest = PutRecordBatchRequest {
             deliveryStreamName = streamName
             records = recordList
        }

        val recordResponse = firehoseClient.putRecordBatch(recordBatchRequest)
        println("The number of records added is ${recordResponse.requestResponses?.size}")


    } catch (ex: FirehoseException) {
        println(ex.message)
        firehoseClient.close()
        exitProcess(0)
    } catch (e: InterruptedException) {
        println(e.localizedMessage)
        exitProcess(0)
    }
}
// snippet-end:[firehose.kotlin.put_batch_records.main]