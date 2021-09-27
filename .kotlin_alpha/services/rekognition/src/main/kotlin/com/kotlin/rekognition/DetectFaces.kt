// snippet-sourcedescription:[DetectFaces.kt demonstrates how to detect faces in an image.]
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

// snippet-start:[rekognition.kotlin.detect_faces.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.Image
import aws.sdk.kotlin.services.rekognition.model.DetectFacesRequest
import aws.sdk.kotlin.services.rekognition.model.Attribute
import aws.sdk.kotlin.services.rekognition.model.RekognitionException
import aws.sdk.kotlin.services.rekognition.model.FaceDetail
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.detect_faces.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>){

    val usage = """
        Usage: 
            <sourceImage> 

        Where:
            sourceImage - the path to the source image (for example, C:\AWS\pic1.png). 
     """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val sourceImage = args[0]
    val rekClient = RekognitionClient{ region = "us-east-1"}
    detectFacesinImage(rekClient, sourceImage)
    rekClient.close()
}

// snippet-start:[rekognition.kotlin.detect_faces.main]
suspend fun detectFacesinImage(rekClient: RekognitionClient, sourceImage: String?) {
    try {
        // Create an Image object for the source image
        val souImage = Image {
            bytes = (File(sourceImage).readBytes())
        }

        val facesRequest = DetectFacesRequest {
            attributes = listOf(Attribute.All)
            image = souImage
        }

        val facesResponse = rekClient.detectFaces(facesRequest)
        val faceDetails = facesResponse.faceDetails

        if (faceDetails != null) {
            for (face: FaceDetail in faceDetails) {
                val ageRange = face.ageRange
                println("The detected face is estimated to be between ${ageRange?.low.toString()} and ${ageRange?.high.toString()} years old.")
                println("There is a smile ${face.smile?.value.toString()}")
            }
        }
    } catch (e: RekognitionException) {
        println(e.message)
        rekClient.close()
        exitProcess(0)
    } catch (e: FileNotFoundException) {
        println(e.message)
        System.exit(1)
    }
}
// snippet-end:[rekognition.kotlin.detect_faces.main]