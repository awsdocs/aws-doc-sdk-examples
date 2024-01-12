// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.handlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.example.photo.PhotoApplicationResources
import com.example.photo.services.AnalyzePhotos
import com.example.photo.services.DynamoDBService
import kotlinx.coroutines.runBlocking

class S3Handler : RequestHandler<S3Event, String> {

    override fun handleRequest(event: S3Event, context: Context): String? = runBlocking {
        // Get the Amazon Simple Storage Service (Amazon S3) bucket and object key from the Amazon S3 event.
        val bucketName = event.records[0].s3.bucket.name
        val objectKey = event.records[0].s3.getObject().key

        // Log the S3 bucket and object key in the log file.
        context.logger.log("S3 object name: s3://$bucketName/$objectKey")
        val photos = AnalyzePhotos()
        val dbService = DynamoDBService()

        // Tag the file.
        val labels = photos.detectLabels(PhotoApplicationResources.STORAGE_BUCKET, objectKey)
        dbService.putRecord(labels)
        context.logger.log("Tagged image")
        return@runBlocking "OK"
    }
}
