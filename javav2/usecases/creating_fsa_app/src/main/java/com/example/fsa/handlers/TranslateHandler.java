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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateHandler implements RequestHandler<Map<String, Object>, JSONObject> {

    @Override
    public JSONObject handleRequest(Map<String, Object> requestObject, Context context) {
        TranslateService translateService = new TranslateService();
        String preValStr = requestObject.toString();
        context.getLogger().log("Pre Value: " + preValStr);
        String sourceText = getTranslatedText(preValStr);
        context.getLogger().log("NEW Value: " + sourceText);

        // We have the source text - need to figure out what language its in.
        DetectSentimentService sentimentService = new DetectSentimentService();
        String lanCode = sentimentService.detectTheDominantLanguage(sourceText);
        String translatedText;
        translatedText = translateService.translateText(lanCode, sourceText);
        context.getLogger().log("Translated text : " + translatedText);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("translated_text", translatedText);
        return jsonResponse;
    }

    private String getTranslatedText(String myString) {
        String extractedValue;
        Pattern pattern = Pattern.compile("extracted_text\\s*=\\s*([^,}]*)");
        Matcher matcher = pattern.matcher(myString);
        if (matcher.find()) {
            extractedValue = matcher.group(1);
            return extractedValue;
        }
        return "";
    }
}