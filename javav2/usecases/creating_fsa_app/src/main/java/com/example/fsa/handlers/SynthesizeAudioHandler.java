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

public class SynthesizeAudioHandler implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> requestObject, Context context) {
        S3Service s3Service = new S3Service();
        PollyService pollyService = new PollyService();
        String translatedText = (String) requestObject.get("translated_text");
        String bucket = (String) requestObject.get("bucket");
        String key = (String) requestObject.get("object");
        key = key + ".mp3"; // Appends ".mp3" to the existing value of key
        context.getLogger().log("*** Translated Text: " +translatedText +" and new key is "+key);
        try {
            InputStream is = pollyService.synthesize(translatedText);
            String audioFile = s3Service.putAudio(is, bucket, key);
            context.getLogger().log("You have successfully added the " +audioFile +"  in "+bucket);
            return audioFile ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
