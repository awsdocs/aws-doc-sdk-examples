/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.photo

import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DetectLabelsRequest
import aws.sdk.kotlin.services.rekognition.model.Image
import org.springframework.stereotype.Component

@Component
class AnalyzePhotos {

    suspend fun DetectLabels(bytesVal: ByteArray?, key: String?): MutableList<WorkItem>? {

        // Create an Image object for the source image.
        val souImage = Image {
            bytes = bytesVal
        }

        val detectLabelsRequest = DetectLabelsRequest {
            image = souImage
            maxLabels = 10
        }

        RekognitionClient { region = "us-west-2" }.use { rekClient ->
            val response = rekClient.detectLabels(detectLabelsRequest)

            // Write the results to a WorkItem instance.
            val list = mutableListOf<WorkItem>()
            println("Detected labels for the given photo")
            response.labels?.forEach { label ->
                val item = WorkItem()
                item.key = key // identifies the photo.
                item.confidence = label.confidence.toString()
                item.name = label.name
                list.add(item)
            }
            return list
        }
    }
}
