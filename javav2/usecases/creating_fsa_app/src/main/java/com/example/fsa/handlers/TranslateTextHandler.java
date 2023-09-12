/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.services.TranslateService;
import org.json.simple.JSONObject;
import java.util.Map;

public class TranslateTextHandler implements RequestHandler<Map<String, Object>, JSONObject> {

    @Override
    public JSONObject handleRequest(Map<String, Object> requestObject, Context context) {
        TranslateService translateService = new TranslateService();
        String sourceText = (String) requestObject.get("extracted_text");
        String lanCode = (String) requestObject.get("source_language_code");
        context.getLogger().log("sourceText: " + sourceText + "lang code: "+lanCode);
        String translatedText = translateService.translateText(lanCode, sourceText);
        context.getLogger().log("Translated text : " + translatedText);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("translated_text", translatedText);
        return jsonResponse;
    }
}