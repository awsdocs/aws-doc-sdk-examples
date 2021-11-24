//snippet-sourcedescription:[DeleteDataStream.kt demonstrates how to delete an Amazon Kinesis data stream.]
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

//snippet-start:[kinesis.kotlin.delete.import]
import aws.sdk.kotlin.services.kinesis.KinesisClient
import aws.sdk.kotlin.services.kinesis.model.DeleteStreamRequest
import kotlin.system.exitProcess
//snippet-end:[kinesis.kotlin.delete.import]

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

    val streamName = args[0]
    deleteStream(streamName)
   }

//snippet-start:[kinesis.kotlin.delete.main]
suspend fun deleteStream(streamNameVal: String?) {

    val request = DeleteStreamRequest {
        streamName = streamNameVal
    }

    KinesisClient { region = "us-east-1" }.use { kinesisClient ->
        kinesisClient.deleteStream(request)
        println("$streamNameVal was deleted.")
    }
}
//snippet-end:[kinesis.kotlin.delete.main]