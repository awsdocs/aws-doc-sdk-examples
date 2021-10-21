//snippet-sourcedescription:[CreateDeliveryStream.kt demonstrates how to create a delivery stream.]
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

// snippet-start:[firehose.kotlin.create_stream.import]
import aws.sdk.kotlin.services.firehose.FirehoseClient
import aws.sdk.kotlin.services.firehose.model.ExtendedS3DestinationConfiguration
import aws.sdk.kotlin.services.firehose.model.CreateDeliveryStreamRequest
import aws.sdk.kotlin.services.firehose.model.DeliveryStreamType
import aws.sdk.kotlin.services.firehose.model.FirehoseException
import kotlin.system.exitProcess
// snippet-end:[firehose.kotlin.create_stream.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    
    Usage:
        <bucketARN> <roleARN> <streamName> 

    Where:
        bucketARN - the ARN of the Amazon S3 bucket where the data stream is written. 
        roleARN - the ARN of the IAM role that has the permissions that Kinesis Data Firehose needs. 
        streamName - the name of the delivery stream. 
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val bucketARN = args[0]
    val roleARN = args[1]
    val streamName = args[2]

    val firehoseClient = FirehoseClient{region="us-east-1"}
    createStream(firehoseClient, bucketARN, roleARN, streamName)
    firehoseClient.close()
}

// snippet-start:[firehose.kotlin.create_stream.main]
suspend fun createStream(firehoseClient: FirehoseClient, bucketARNVal: String?, roleARNVal: String?, streamName: String?) {
    try {

        val destinationConfiguration = ExtendedS3DestinationConfiguration {
            bucketArn = bucketARNVal
            roleArn = roleARNVal
        }

        val deliveryStreamRequest= CreateDeliveryStreamRequest {
            deliveryStreamName = streamName
            extendedS3DestinationConfiguration = destinationConfiguration
            deliveryStreamType = DeliveryStreamType.DirectPut
        }

        val streamResponse = firehoseClient.createDeliveryStream(deliveryStreamRequest)
        println("Delivery Stream ARN is ${streamResponse.deliveryStreamArn}")

    } catch (ex: FirehoseException) {
        println(ex.message)
        firehoseClient.close()
        exitProcess(0)
    }
}
// snippet-end:[firehose.kotlin.create_stream.main]