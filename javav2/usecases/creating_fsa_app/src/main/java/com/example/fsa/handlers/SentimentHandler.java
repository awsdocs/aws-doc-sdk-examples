/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.services.DetectSentimentService;
import org.json.simple.JSONObject;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentimentHandler implements RequestHandler<Map<String, Object>, JSONObject> {

    @Override
    public JSONObject handleRequest(Map<String, Object> requestObject, Context context) {
        // Log the entire JSON input.
        String inputString = requestObject.toString();
        context.getLogger().log("Received JSON: " + inputString);
        String value = extractValueFromRequestObject(inputString);
        context.getLogger().log("Extracted text: " + value);
        DetectSentimentService detectSentimentService = new DetectSentimentService();
        JSONObject jsonOb = detectSentimentService.detectSentiments(value);
        context.getLogger().log("NEW JSON: " + jsonOb.toJSONString());
        return jsonOb;
    }

    private static String extractValueFromRequestObject(String inputString) {
        Matcher matcher = Pattern.compile("source_text=([^,}]+)").matcher(inputString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}

