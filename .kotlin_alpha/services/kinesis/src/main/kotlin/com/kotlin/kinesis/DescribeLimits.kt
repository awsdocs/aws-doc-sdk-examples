//snippet-sourcedescription:[DescribeLimits.kt demonstrates how to display the shard limit and usage for a given account.]
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

//snippet-start:[kinesis.kotlin.DescribeLimits.import]
import aws.sdk.kotlin.services.kinesis.KinesisClient
import aws.sdk.kotlin.services.kinesis.model.KinesisException
import aws.sdk.kotlin.services.kinesis.model.DescribeLimitsRequest
import kotlin.system.exitProcess
//snippet-end:[kinesis.kotlin.DescribeLimits.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun  main(){

    val kinesisClient = KinesisClient{region ="us-east-1"}
    describeKinLimits(kinesisClient)
    kinesisClient.close()
}

//snippet-start:[kinesis.kotlin.DescribeLimits.main]
suspend fun describeKinLimits(kinesisClient: KinesisClient) {

    try {
        val response = kinesisClient.describeLimits(DescribeLimitsRequest{})
        println("Number of open shards is ${response.openShardCount}")
        println("Maximum shards allowed is ${response.shardLimit}")

    } catch (e: KinesisException) {
        System.err.println(e.message)
        exitProcess(0)
    }
    println("Done")
}
//snippet-end:[kinesis.kotlin.DescribeLimits.main]