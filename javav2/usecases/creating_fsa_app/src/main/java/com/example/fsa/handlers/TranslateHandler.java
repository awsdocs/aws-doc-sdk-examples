/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.services.DetectSentimentService;
import com.example.fsa.services.TranslateService;
import org.json.simple.JSONObject;
import java.util.Map;

public class TranslateHandler implements RequestHandler<Map<String, Object>, JSONObject> {

    @Override
    public JSONObject handleRequest(Map<String, Object> requestObject, Context context) {
        TranslateService translateService = new TranslateService();
        String sourceText = (String) requestObject.get("extracted_text");
        context.getLogger().log("NEW Value: " + sourceText);

        // We have the source text - need to figure out what language it's in.
        DetectSentimentService sentimentService = new DetectSentimentService();
        String lanCode = sentimentService.detectTheDominantLanguage(sourceText);
        String translatedText = translateService.translateText(lanCode, sourceText);
        context.getLogger().log("Translated text : " + translatedText);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("translated_text", translatedText);
        return jsonResponse;
    }
}