//snippet-sourcedescription:[StockTradesWriter.kt demonstrates how to write multiple data records into an Amazon Kinesis data stream.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.kinesis

//snippet-start:[kinesis.kotlin.putrecord.import]
import aws.sdk.kotlin.services.kinesis.KinesisClient
import aws.sdk.kotlin.services.kinesis.model.PutRecordRequest
import aws.sdk.kotlin.services.kinesis.model.DescribeStreamRequest
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
//snippet-end:[kinesis.kotlin.putrecord.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun  main(args: Array<String>){

    val usage = """
    Usage: <streamName>

    Where:
        streamName - The Amazon Kinesis data stream (for example, StockTradeStream)
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
     }

    val streamName = args[0]
    validateStream(streamName)
    setStockData(streamName)
    }

//snippet-start:[kinesis.kotlin.putrecord.main]
suspend fun setStockData(streamName: String) {

        // Repeatedly send stock trades with a 100 milliseconds wait in between.
        val stockTradeGenerator = StockTradeGenerator()

        // Put in 50 Records for this example.
        val index = 50
        for (x in 0 until index) {
            val trade: StockTrade = stockTradeGenerator.getSampleData()
            sendStockTrade(trade, streamName)
            delay(100)
        }
}

private suspend fun sendStockTrade( trade: StockTrade, streamNameVal: String ) {

    val bytes = trade.toJsonAsBytes()

    // The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
    if (bytes == null) {
        println("Could not get JSON bytes for stock trade")
        return
    }
    println("Putting trade: $trade")
    val request = PutRecordRequest {
        partitionKey = trade.getTheTickerSymbol() // We use the ticker symbol as the partition key, explained in the Supplemental Information section below.
        streamName = streamNameVal
        data = bytes
    }

    KinesisClient { region = "us-east-1" }.use { kinesisClient ->
        kinesisClient.putRecord(request)
   }
}

suspend fun validateStream(streamNameVal: String) {

    val request = DescribeStreamRequest {
        streamName = streamNameVal
    }

    KinesisClient { region = "us-east-1" }.use { kinesisClient ->
        val describeStreamResponse = kinesisClient.describeStream(request)

        if (describeStreamResponse.streamDescription?.streamStatus.toString() != "ACTIVE") {
            System.err.println("Stream $streamNameVal is not active. Please wait a few moments and try again.")
            exitProcess(1)
        }
    }
 }
//snippet-end:[kinesis.kotlin.putrecord.main]
