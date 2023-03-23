/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.photo.LabelCount;
import com.example.photo.PhotoApplicationResources;
import com.example.photo.services.AnalyzePhotos;
import com.example.photo.services.DynamoDBService;
import java.util.List;

// Tags the object after it's uploaded into the storage bucket.
public class S3Handler implements RequestHandler<S3Event, String> {
    @Override
    public String handleRequest(S3Event event, Context context) {
        // Get the Amazon Simple Storage Service (Amazon S3) bucket and object key from the Amazon S3 event.
        String bucketName = event.getRecords().get(0).getS3().getBucket().getName();
        String objectKey = event.getRecords().get(0).getS3().getObject().getKey();

        // Log the S3 bucket and object key in the log file.
        context.getLogger().log("S3 object name: s3://" + bucketName + "/" + objectKey);
        AnalyzePhotos photos = new AnalyzePhotos();
        DynamoDBService dbService = new DynamoDBService();

        // Tag the file.
        List<LabelCount> labels = photos.detectLabels(PhotoApplicationResources.STORAGE_BUCKET, objectKey);
        dbService.putRecord(labels);
        context.getLogger().log("Tagged image");
        return "OK";
    }
}
