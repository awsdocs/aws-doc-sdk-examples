// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.kinesis

// snippet-start:[kinesis.kotlin.get_records.import]
import aws.sdk.kotlin.services.kinesis.KinesisClient
import aws.sdk.kotlin.services.kinesis.model.DescribeStreamRequest
import aws.sdk.kotlin.services.kinesis.model.DescribeStreamResponse
import aws.sdk.kotlin.services.kinesis.model.GetRecordsRequest
import aws.sdk.kotlin.services.kinesis.model.GetShardIteratorRequest
import aws.sdk.kotlin.services.kinesis.model.Shard
import aws.sdk.kotlin.services.kinesis.model.ShardIteratorType
import kotlin.system.exitProcess
// snippet-end:[kinesis.kotlin.get_records.import]

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
        streamName - The Amazon Kinesis data stream (for example, StockTradeStream).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val streamName = args[0]
    getStockTrades(streamName)
}

// snippet-start:[kinesis.kotlin.get_records.main]
suspend fun getStockTrades(streamNameVal: String?) {
    val shardIteratorVal: String
    val shards = mutableListOf<Shard?>()
    var streamRes: DescribeStreamResponse
    val request =
        DescribeStreamRequest {
            streamName = streamNameVal
        }
    KinesisClient { region = "us-east-1" }.use { kinesisClient ->
        do {
            streamRes = kinesisClient.describeStream(request)
            shards.add(streamRes.streamDescription?.shards?.get(0))
        } while (streamRes.streamDescription?.hasMoreShards == true)

        val id = shards[0]?.shardId
        val shardIteratorResult =
            kinesisClient.getShardIterator(
                GetShardIteratorRequest {
                    streamName = streamNameVal
                    shardIteratorType = ShardIteratorType.fromValue("TRIM_HORIZON")
                    shardId = id
                },
            )
        shardIteratorVal = shardIteratorResult.shardIterator.toString()

        val recRequest =
            GetRecordsRequest {
                shardIterator = shardIteratorVal
                limit = 1000
            }

        // Continuously read data records from shard.
        val result = kinesisClient.getRecords(recRequest)
        result.records?.forEach { record ->
            println("Seq No: ${record.sequenceNumber} - ${record.data?.let { String(it) }}")
        }
    }
}
// snippet-end:[kinesis.kotlin.get_records.main]
