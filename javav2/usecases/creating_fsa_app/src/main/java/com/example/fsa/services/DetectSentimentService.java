/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;
import org.json.simple.JSONObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageRequest;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageResponse;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
import software.amazon.awssdk.services.comprehend.model.DominantLanguage;
import java.util.List;

public class DetectSentimentService {

    private static ComprehendClient comprehendClient;
    private static synchronized ComprehendClient getComprehendClient() {
        if (comprehendClient == null) {
            comprehendClient = ComprehendClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return comprehendClient;
    }

    public JSONObject detectSentiments(String text){
        try {
            String languageCode = detectTheDominantLanguage(text);
            DetectSentimentRequest detectSentimentRequest = DetectSentimentRequest.builder()
                .text(text)
                .languageCode(languageCode)
                .build();

            DetectSentimentResponse detectSentimentResult = getComprehendClient().detectSentiment(detectSentimentRequest);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sentiment", detectSentimentResult.sentimentAsString());
            jsonObject.put("language_code", languageCode);
            return jsonObject;

        } catch (ComprehendException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return null;
    }

    public String detectTheDominantLanguage(String text){
        try {
            DetectDominantLanguageRequest request = DetectDominantLanguageRequest.builder()
                .text(text)
                .build();

            DetectDominantLanguageResponse resp = getComprehendClient().detectDominantLanguage(request);
            List<DominantLanguage> allLanList = resp.languages();
            if (!allLanList.isEmpty()) {
                DominantLanguage firstLanguage = allLanList.get(0);
                return firstLanguage.languageCode();
            } else {
                // Handle the case where the list is empty.
                return "No languages found";
            }

        } catch (ComprehendException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }
}
