// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.endpoints

import com.example.photo.PhotoApplicationResources
import com.example.photo.services.AnalyzePhotos
import com.example.photo.services.DynamoDBService
import com.example.photo.services.S3Service

class UploadEndpoint {
    suspend fun upload(bytes: ByteArray, name: String) {
        // Put the file into the bucket.
        val s3Service = S3Service()
        val analyzePhotos = AnalyzePhotos()
        val dbService = DynamoDBService()

        s3Service?.putObject(bytes, name)
        val labels = analyzePhotos.detectLabels(PhotoApplicationResources.STORAGE_BUCKET, name)
        dbService.putRecord(labels)
    }
}
