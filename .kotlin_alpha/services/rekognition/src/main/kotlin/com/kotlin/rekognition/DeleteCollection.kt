// snippet-sourcedescription:[DeleteCollection.kt demonstrates how to delete an Amazon Rekognition collection.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[06-08-2021]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.delete_collection.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DeleteCollectionRequest
import aws.sdk.kotlin.services.rekognition.model.RekognitionException
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.delete_collection.import]

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
        collectionId - the id of the collection to delete. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val collectionId = args[0]
    val rekClient = RekognitionClient{ region = "us-east-1"}
    println("Deleting collection $collectionId")
    deleteMyCollection(rekClient, collectionId)
    rekClient.close()
}

// snippet-start:[rekognition.kotlin.delete_collection.main]
suspend fun deleteMyCollection(rekClient: RekognitionClient, collectionIdVal: String) {
    try {
        val deleteCollectionRequest = DeleteCollectionRequest {
            collectionId = collectionIdVal
        }

        val deleteCollectionResponse = rekClient.deleteCollection(deleteCollectionRequest)
        println("The collectionId status is ${deleteCollectionResponse.statusCode.toString()}")

    } catch (e: RekognitionException) {
        println(e.message)
        rekClient.close()
        exitProcess(0)
    }
}
// snippet-end:[rekognition.kotlin.delete_collection.main]