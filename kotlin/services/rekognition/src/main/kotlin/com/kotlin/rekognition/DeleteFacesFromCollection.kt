// snippet-sourcedescription:[DeleteFacesFromCollection.kt demonstrates how to delete faces from an Amazon Rekognition collection.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.delete_faces_collection.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DeleteFacesRequest
// snippet-end:[rekognition.kotlin.delete_faces_collection.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
         <collectionId> <faceId> 

    Where:
        collectionId - The id of the collection from which faces are deleted. 
        faceId - The id of the face to delete. 
    """

    if (args.size != 2) {
        println(usage)
        System.exit(1)
    }

    val collectionId = args[0]
    val faceId = args[1]
    deleteFacesCollection(collectionId, faceId)
}

// snippet-start:[rekognition.kotlin.delete_faces_collection.main]
suspend fun deleteFacesCollection(collectionIdVal: String?, faceIdVal: String) {

    val deleteFacesRequest = DeleteFacesRequest {
        collectionId = collectionIdVal
        faceIds = listOf(faceIdVal)
    }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        rekClient.deleteFaces(deleteFacesRequest)
        println("$faceIdVal was deleted from the collection")
    }
}
// snippet-end:[rekognition.kotlin.delete_faces_collection.main]
