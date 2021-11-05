// snippet-sourcedescription:[DetectLabels.kt demonstrates how to capture labels (like water and mountains) in a given image.]
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

// snippet-start:[rekognition.kotlin.detect_labels.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.Image
import aws.sdk.kotlin.services.rekognition.model.DetectLabelsRequest
import aws.sdk.kotlin.services.rekognition.model.RekognitionException
import java.io.FileNotFoundException
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.detect_labels.import]

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
    detectImageLabels(rekClient, sourceImage)
    rekClient.close()

}

// snippet-start:[rekognition.kotlin.detect_labels.main]
suspend fun detectImageLabels(rekClient: RekognitionClient, sourceImage: String) {
    try {

        // Create an Image object for the source image.
        val souImage = Image {
            bytes = (File(sourceImage).readBytes())
        }
        val detectLabelsRequest = DetectLabelsRequest {
            image = souImage
            maxLabels = 10
        }

        val response = rekClient.detectLabels(detectLabelsRequest)
        response.labels?.forEach { label ->
               println("${label.name.toString()} : ${label.confidence.toString()}")
        }

    } catch (e: RekognitionException) {
        println(e.message)
        rekClient.close()
        exitProcess(0)
    } catch (e: FileNotFoundException) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[rekognition.kotlin.detect_labels.main]