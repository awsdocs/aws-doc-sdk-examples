// snippet-sourcedescription:[DeleteCollection.kt demonstrates how to delete an Amazon Rekognition collection.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.delete_collection.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DeleteCollectionRequest
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.delete_collection.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
        <collectionId> 

    Where:
        collectionId - The id of the collection to delete. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val collectionId = args[0]
    println("Deleting collection $collectionId")
    deleteMyCollection(collectionId)
}

// snippet-start:[rekognition.kotlin.delete_collection.main]
suspend fun deleteMyCollection(collectionIdVal: String) {

    val request = DeleteCollectionRequest {
        collectionId = collectionIdVal
    }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        val response = rekClient.deleteCollection(request)
        println("The collectionId status is ${response.statusCode}")
    }
}
// snippet-end:[rekognition.kotlin.delete_collection.main]
