// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.endpoints

import com.example.photo.PhotoApplicationResources
import com.example.photo.services.DynamoDBService
import com.example.photo.services.S3Service
import com.example.photo.services.SnsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.withContext
import java.util.UUID

class DownloadEndpoint {
    suspend fun downloadFiles(labels: List<String>): String {
        println("We have the tags")
        val dbService = DynamoDBService()
        val s3Service = S3Service()
        val snsService = SnsService()
        val imageMap: HashMap<String, ByteArray> = HashMap()

        // Now that you have an image list, place the images into a ZIP and presign the ZIP.
        val images = labels.asFlow()
            .flatMapConcat { label ->
                withContext(Dispatchers.IO) {
                    dbService.getImagesByLabel(label)
                }.asFlow()
            }
            .toSet()

        val uuid = UUID.randomUUID().toString()
        val zipName = "$uuid.zip"
        // Populate a Kotlin Collection with the byte[].
        for (image in images) {
            val imageBytes = s3Service.getObjectBytes(PhotoApplicationResources.STORAGE_BUCKET, image)
            if (imageBytes != null) {
                imageMap.put(image, imageBytes)
                println("Added $image to the map")
            }
        }

        // Place the ZIP file into the working bucket and get back a presigned URL.
        val myZipStream = s3Service.createZipFile(imageMap)
        s3Service.putZIP(myZipStream, zipName)
        val presignUrl = s3Service.signObjectToDownload(zipName)
        val message = "Your Archived images can be located here $presignUrl"
        snsService.pubTopic(message)
        return presignUrl.toString()
    }
}
