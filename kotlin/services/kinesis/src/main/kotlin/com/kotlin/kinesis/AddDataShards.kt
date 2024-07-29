// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.kinesis

// snippet-start:[kinesis.kotlin.AddDataShards.import]
import aws.sdk.kotlin.services.kinesis.KinesisClient
import aws.sdk.kotlin.services.kinesis.model.ScalingType
import aws.sdk.kotlin.services.kinesis.model.UpdateShardCountRequest
import kotlin.system.exitProcess
// snippet-end:[kinesis.kotlin.AddDataShards.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
    Usage: <streamName>

    Where:
        streamName - The Amazon Kinesis data stream (for example, StockTradeStream).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val streamName = args[0]
    val inputShards = 1
    addShards(streamName, inputShards)
}

// snippet-start:[kinesis.kotlin.AddDataShards.main]
suspend fun addShards(
    name: String?,
    goalShards: Int,
) {
    val request =
        UpdateShardCountRequest {
            scalingType = ScalingType.fromValue("UNIFORM_SCALING")
            streamName = name
            targetShardCount = goalShards
        }

    KinesisClient { region = "us-east-1" }.use { kinesisClient ->
        val response = kinesisClient.updateShardCount(request)
        println("${response.streamName} has updated shard count to ${response.currentShardCount}")
    }
}
// snippet-end:[kinesis.kotlin.AddDataShards.main]
