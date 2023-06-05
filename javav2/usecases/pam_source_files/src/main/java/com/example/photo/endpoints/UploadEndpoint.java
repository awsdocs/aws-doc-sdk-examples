/*
 Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.endpoints;

import com.example.photo.PhotoApplicationResources;
import com.example.photo.LabelCount;
import com.example.photo.services.AnalyzePhotos;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;

import java.util.List;

public class UploadEndpoint {
    final AnalyzePhotos analyzePhotos;
    final DynamoDBService dbService;
    final S3Service s3Service;

    public UploadEndpoint(AnalyzePhotos analyzePhotos, DynamoDBService dynamoDBService, S3Service s3Service) {
        this.analyzePhotos = analyzePhotos;
        this.dbService = dynamoDBService;
        this.s3Service = s3Service;
    }

    // Places the labels in an Amazon DynamoDB table.
    public void tagAfterUpload(String name) {
        List<LabelCount> labels = analyzePhotos.detectLabels(PhotoApplicationResources.STORAGE_BUCKET, name);
        dbService.putRecord(labels);
    }

    // Put the image into the Amazon S3 bucket.
    public void upload(byte[] bytes, String name) {
        s3Service.putObject(bytes, PhotoApplicationResources.STORAGE_BUCKET, name);
        this.tagAfterUpload(name);
    }
}
