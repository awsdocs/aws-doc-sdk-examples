// snippet-sourcedescription:[DescribeCollection.kt demonstrates how to retrieve the description of an Amazon Rekognition collection.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11-05-2021]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.describe_collection.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DescribeCollectionRequest
import aws.sdk.kotlin.services.rekognition.model.RekognitionException
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.describe_collection.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */


suspend fun main(args: Array<String>){

    val usage = """
    Usage: 
        <collectionId> 

    Where:
        collectionId - the id of the collection to describe. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val collectionId = args[0]
    val rekClient = RekognitionClient{ region = "us-east-1"}
    println("Decribing $collectionId")
    describeColl(rekClient, collectionId)
    rekClient.close()
}

// snippet-start:[rekognition.kotlin.describe_collection.main]
suspend  fun describeColl(rekClient: RekognitionClient, collectionName: String?) {
    try {
        val describeCollectionRequest = DescribeCollectionRequest {
            collectionId = collectionName
        }

        val describeCollectionResponse = rekClient.describeCollection(describeCollectionRequest)
        println("The collection Arn is ${describeCollectionResponse.collectionArn}" )
        println("The collection contains this many faces ${describeCollectionResponse.faceCount}")

    } catch (e: RekognitionException) {
        println(e.message)
        rekClient.close()
        exitProcess(0)
    }
}
// snippet-end:[rekognition.kotlin.describe_collection.main]