// snippet-sourcedescription:[DetectModerationLabels.kt demonstrates how to detect unsafe content in an image.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.detect_mod_labels.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DetectModerationLabelsRequest
import aws.sdk.kotlin.services.rekognition.model.Image
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.detect_mod_labels.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: <sourceImage> 

        Where:
            "sourceImage - The path to the image (for example, C:\\AWS\\pic1.png).
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val sourceImage = args[0]
    detectModLabels(sourceImage)
}

// snippet-start:[rekognition.kotlin.detect_mod_labels.main]
suspend fun detectModLabels(sourceImage: String) {

    val myImage = Image {
        this.bytes = (File(sourceImage).readBytes())
    }

    val request = DetectModerationLabelsRequest {
        image = myImage
        minConfidence = 60f
    }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        val response = rekClient.detectModerationLabels(request)
        response.moderationLabels?.forEach { label ->
            println("Label: ${label.name} - Confidence: ${label.confidence} % Parent: ${label.parentName}")
        }
    }
}
// snippet-end:[rekognition.kotlin.detect_mod_labels.main]
