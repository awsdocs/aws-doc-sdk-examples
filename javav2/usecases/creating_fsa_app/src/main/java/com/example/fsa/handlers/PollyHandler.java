/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.services.PollyService;
import com.example.fsa.services.S3Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class PollyHandler implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> requestObject, Context context) {
        S3Service s3Service = new S3Service();
        PollyService pollyService = new PollyService();
        String translatedText = (String) requestObject.get("translated_text");
        String bucket = (String) requestObject.get("bucket");
        String key = (String) requestObject.get("object");
        String newFileName = convertFileEx(key);
        context.getLogger().log("*** Translated Text: " +translatedText +" and new key is "+newFileName);
        try {
            InputStream is = pollyService.synthesize(translatedText);
            String audioFile = s3Service.putAudio(is, bucket, newFileName);
            context.getLogger().log("You have successfully added the " +audioFile +"  in "+bucket);
            return audioFile ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertFileEx(String originalFileName) {
        // Find the last occurrence of the dot (.) in the file name
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            // Remove the existing extension and append "mp3".
            return originalFileName.substring(0, lastDotIndex) + ".mp3";
        } else {
            // If there's no existing extension, simply append ".mp3"
            return originalFileName + ".mp3";
        }
    }
}
