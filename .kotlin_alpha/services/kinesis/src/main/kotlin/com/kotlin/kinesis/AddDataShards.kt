//snippet-sourcedescription:[AddDataShards.kt demonstrates how to increase shard count in an Amazon Kinesis data stream.]
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

//snippet-start:[kinesis.kotlin.AddDataShards.import]
import aws.sdk.kotlin.services.kinesis.KinesisClient
import aws.sdk.kotlin.services.kinesis.model.KinesisException
import aws.sdk.kotlin.services.kinesis.model.ScalingType
import aws.sdk.kotlin.services.kinesis.model.UpdateShardCountRequest
import kotlin.system.exitProcess
//snippet-end:[kinesis.kotlin.AddDataShards.import]

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
        exitProcess(0)
    }

    val name = args[0]
    val kinesisClient = KinesisClient{region ="us-east-1"}
    val inputShards = 1
    addShards(kinesisClient, name, inputShards)
    kinesisClient.close()

}

//snippet-start:[kinesis.kotlin.AddDataShards.main]
suspend fun addShards(kinesisClient: KinesisClient, name: String?, goalShards: Int) {
    try {
        val request = UpdateShardCountRequest {
            scalingType = ScalingType.fromValue("UNIFORM_SCALING")
            streamName = name
            targetShardCount= goalShards
        }

        val response = kinesisClient.updateShardCount(request)
        println("${response.streamName} has updated shard count to ${response.currentShardCount}")

    } catch (e: KinesisException) {
        println(e.message)
        kinesisClient.close()
        exitProcess(0)
    }
}
//snippet-end:[kinesis.kotlin.AddDataShards.main]