// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.services

import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DetectLabelsRequest
import aws.sdk.kotlin.services.rekognition.model.Image
import aws.sdk.kotlin.services.rekognition.model.S3Object
import com.example.photo.LabelCount

class AnalyzePhotos {
    suspend fun detectLabels(bucketName: String?, key: String?): ArrayList<LabelCount> {
        val s3Ob = S3Object {
            bucket = bucketName
            name = key
        }

        val souImage = Image {
            s3Object = s3Ob
        }

        val detectLabelsRequest = DetectLabelsRequest {
            image = souImage
            maxLabels = 10
        }

        RekognitionClient { region = "us-east-1" }.use { rekClient ->
            val labelsResponse = rekClient.detectLabels(detectLabelsRequest)
            val labels = labelsResponse.labels
            println("Detected labels for the given photo")
            val list = ArrayList<LabelCount>()
            var item: LabelCount
            if (labels != null) {
                for (label in labels) {
                    item = LabelCount()
                    item.setKey(key)
                    item.setName(label.name)
                    list.add(item)
                }
            }
            return list
        }
    }
}
