/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.S3Object;

public class ExtractTextService {

    public String getCardText(String bucketName, String obName) {
        Region region = Region.US_EAST_1;
        TextractClient textractClient = TextractClient.builder()
            .region(region)
            .build();

        S3Object s3Object = S3Object.builder()
            .bucket(bucketName)
            .name(obName)
            .build();

        // Create a Document object and reference the s3Object instance.
        Document myDoc = Document.builder()
            .s3Object(s3Object)
            .build();

        DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
            .document(myDoc)
            .build();

        // Use StringBuilder to build the complete text.
        StringBuilder completeText = new StringBuilder();
        DetectDocumentTextResponse textResponse = textractClient.detectDocumentText(detectDocumentTextRequest);
        for (Block block : textResponse.blocks()) {
            if (block.blockType() == BlockType.WORD) {
                if (completeText.length() == 0) {
                    completeText.append(block.text());
                } else {
                    completeText.append(" ").append(block.text());
                }
            }
        }
       return completeText.toString();
    }
}
