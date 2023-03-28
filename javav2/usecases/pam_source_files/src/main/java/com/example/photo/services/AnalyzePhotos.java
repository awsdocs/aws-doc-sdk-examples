/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.photo.services;

import com.example.photo.PhotoApplicationResources;
import com.example.photo.LabelCount;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.services.rekognition.model.S3Object;

public class AnalyzePhotos {
    public ArrayList<LabelCount> detectLabels(String bucketName, String key) {
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(PhotoApplicationResources.REGION)
                .build();
        try {
            S3Object s3Object = S3Object.builder()
                    .bucket(bucketName)
                    .name(key)
                    .build();

            Image souImage = Image.builder()
                    .s3Object(s3Object)
                    .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(souImage)
                    .maxLabels(10)
                    .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            List<Label> labels = labelsResponse.labels();
            System.out.println("Detected labels for the given photo");
            ArrayList<LabelCount> list = new ArrayList<>();
            LabelCount item;
            for (Label label : labels) {
                item = new LabelCount();
                item.setKey(key);
                item.setName(label.name());
                list.add(item);
            }
            return list;

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}