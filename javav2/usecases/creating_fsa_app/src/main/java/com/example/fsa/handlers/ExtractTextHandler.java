/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.services.ExtractTextService;
import java.util.Map;

public class ExtractTextHandler implements RequestHandler<Map<String, Object>, String>{

    @Override
    public String handleRequest(Map<String, Object> requestObject, Context context) {
        // Get the Amazon Simple Storage Service (Amazon S3) bucket and object key from the Amazon EventBridge event.
        ExtractTextService textService = new ExtractTextService();
        String bucket = (String) requestObject.get("bucket");
        String fileName = (String) requestObject.get("object");
        context.getLogger().log("*** Bucket: " + bucket + ", fileName: " + fileName);
        String extractedText = textService.getCardText(bucket, fileName);
        context.getLogger().log("*** Text: " + extractedText);
        return extractedText;
    }
}
