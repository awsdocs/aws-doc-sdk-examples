// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.add_faces_collection.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.Attribute
import aws.sdk.kotlin.services.rekognition.model.Image
import aws.sdk.kotlin.services.rekognition.model.IndexFacesRequest
import aws.sdk.kotlin.services.rekognition.model.QualityFilter
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.add_faces_collection.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """
    Usage: 
        <collectionId> <sourceImage>

    Where:
        collectionName - The name of the collection.
        sourceImage - The path to the image (for example, C:\AWS\pic1.png). 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val collectionId = args[0]
    val sourceImage = args[1]
    addToCollection(collectionId, sourceImage)
}

// snippet-start:[rekognition.kotlin.add_faces_collection.main]
suspend fun addToCollection(
    collectionIdVal: String?,
    sourceImage: String,
) {
    val souImage =
        Image {
            bytes = (File(sourceImage).readBytes())
        }

    val request =
        IndexFacesRequest {
            collectionId = collectionIdVal
            image = souImage
            maxFaces = 1
            qualityFilter = QualityFilter.Auto
            detectionAttributes = listOf(Attribute.Default)
        }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        val facesResponse = rekClient.indexFaces(request)

        // Display the results.
        println("Results for the image")
        println("\n Faces indexed:")
        facesResponse.faceRecords?.forEach { faceRecord ->
            println("Face ID: ${faceRecord.face?.faceId}")
            println("Location: ${faceRecord.faceDetail?.boundingBox}")
        }

        println("Faces not indexed:")
        facesResponse.unindexedFaces?.forEach { unindexedFace ->
            println("Location: ${unindexedFace.faceDetail?.boundingBox}")
            println("Reasons:")

            unindexedFace.reasons?.forEach { reason ->
                println("Reason:  $reason")
            }
        }
    }
}
// snippet-end:[rekognition.kotlin.add_faces_collection.main]
