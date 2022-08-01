// snippet-sourcedescription:[CreateCollection.kt demonstrates how to create an Amazon Rekognition collection.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.create_collection.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.CreateCollectionRequest
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.create_collection.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            <collectionName> 

        Where:
            collectionName - The name of the collection. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val collectionName = args[0]
    createMyCollection(collectionName)
}

// snippet-start:[rekognition.kotlin.create_collection.main]
suspend fun createMyCollection(collectionIdVal: String) {

    val request = CreateCollectionRequest {
        collectionId = collectionIdVal
    }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        val response = rekClient.createCollection(request)
        println("Collection ARN is ${response.collectionArn}")
        println("Status code is ${response.statusCode}")
    }
}
// snippet-end:[rekognition.kotlin.create_collection.main]
