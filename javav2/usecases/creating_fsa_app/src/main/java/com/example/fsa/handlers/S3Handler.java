/*
  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
  SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.FSAApplicationResources;
import com.example.fsa.services.ExtractTextService;
import java.util.Map;

public class S3Handler implements RequestHandler<Map<String, Object>, String>{

    @Override
    public String handleRequest(Map<String, Object> requestObject, Context context) {
        // Get the Amazon Simple Storage Service (Amazon S3) bucket and object key from the Amazon S3 event.
        ExtractTextService textService = new ExtractTextService();
        String bucket = (String) requestObject.getOrDefault("bucket", "");
        String fileName = (String) requestObject.getOrDefault("object", "");
        context.getLogger().log("*** Bucket: " + bucket + ", fileName: " + fileName);
        String myText = textService.getCardText(FSAApplicationResources.STORAGE_BUCKET, fileName);
        context.getLogger().log("*** Text: " + myText);
        return myText;
    }
}
