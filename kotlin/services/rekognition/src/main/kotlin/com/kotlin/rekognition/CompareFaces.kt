// snippet-sourcedescription:[CompareFaces.kt demonstrates how to compare 2 faces.]
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

// snippet-start:[rekognition.kotlin.compare_faces.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import  aws.sdk.kotlin.services.rekognition.model.CompareFacesMatch
import aws.sdk.kotlin.services.rekognition.model.CompareFacesRequest
import aws.sdk.kotlin.services.rekognition.model.Image
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.compare_faces.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>){

    val usage = """
        Usage: <pathSource> <pathTarget>

        Where:
            pathSource - the path to the source image (for example, C:\AWS\pic1.png). 
            pathTarget - the path to the target image (for example, C:\AWS\pic2.png). 
    """

     if (args.size != 2) {
         println(usage)
         exitProcess(0)
     }

    val similarityThreshold = 70f
    val sourceImage = args[0]
    val targetImage = args[1]
    compareTwoFaces(similarityThreshold, sourceImage, targetImage)
}

// snippet-start:[rekognition.kotlin.compare_faces.main]
suspend fun compareTwoFaces(similarityThresholdVal: Float, sourceImageVal: String, targetImageVal: String) {

           val sourceBytes = (File(sourceImageVal).readBytes())
           val targetBytes = (File(targetImageVal).readBytes())

            // Create an Image object for the source image.
            val souImage = Image {
                bytes = sourceBytes
            }

            val tarImage = Image {
                bytes = targetBytes
            }

            val facesRequest = CompareFacesRequest {
                sourceImage = souImage
                targetImage = tarImage
                similarityThreshold = similarityThresholdVal
            }

            RekognitionClient { region = "us-east-1" }.use { rekClient ->

              val compareFacesResult = rekClient.compareFaces(facesRequest)
              val faceDetails = compareFacesResult.faceMatches

              if (faceDetails != null) {
                for (match: CompareFacesMatch in faceDetails) {
                    val face = match.face
                    val position = face?.boundingBox
                    if (position != null)
                        println("Face at ${position.left.toString()} ${position.top} matches with ${face.confidence.toString()} % confidence.")
                }
              }

              val uncompared = compareFacesResult.unmatchedFaces
              if (uncompared != null)
                println("There was ${uncompared.size} face(s) that did not match")

               println("Source image rotation: ${compareFacesResult.sourceImageOrientationCorrection}")
               println("target image rotation: ${compareFacesResult.targetImageOrientationCorrection}")
           }
   }
// snippet-end:[rekognition.kotlin.compare_faces.main]