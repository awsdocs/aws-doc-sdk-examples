/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.S3Object;
import software.amazon.awssdk.services.textract.model.TextractException;

public class ExtractTextService {

    private static TextractClient textractClient;

    private static synchronized TextractClient getTextractClient() {
        if (textractClient == null) {
            textractClient = TextractClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return textractClient;
    }

    public String getCardText(String bucketName, String obName) {
        try {
            S3Object s3Object = S3Object.builder()
                .bucket(bucketName)
                .name(obName)
                .build();

            Document myDoc = Document.builder()
                .s3Object(s3Object)
                .build();

            DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
                .document(myDoc)
                .build();

            StringBuilder completeText = new StringBuilder();
            DetectDocumentTextResponse textResponse = getTextractClient().detectDocumentText(detectDocumentTextRequest);
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

        } catch (TextractException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e; // Re-throw the exception.
        } catch (SdkClientException e) {
            System.err.println(e.getMessage());
            throw e; // Re-throw the exception.
        }
    }
}