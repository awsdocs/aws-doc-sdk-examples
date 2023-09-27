/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import org.json.simple.JSONObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendAsyncClient;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageRequest;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageResponse;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
import software.amazon.awssdk.services.comprehend.model.DominantLanguage;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DetectSentimentService {

    private static ComprehendAsyncClient comprehendAsyncClient;
    private static synchronized ComprehendAsyncClient getComprehendAsyncClient() {
        if (comprehendAsyncClient == null) {
            comprehendAsyncClient = ComprehendAsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return comprehendAsyncClient;
    }

    public JSONObject detectSentiments(String text){
        try {
            String languageCode = detectTheDominantLanguage(text);
            DetectSentimentRequest detectSentimentRequest = DetectSentimentRequest.builder()
                .text(text)
                .languageCode(languageCode)
                .build();

            CompletableFuture<?> future  = getComprehendAsyncClient().detectSentiment(detectSentimentRequest);
            future.join();

            // Wait for the operation to complete and get the result
            DetectSentimentResponse detectSentimentResult = (DetectSentimentResponse) future.join();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sentiment", detectSentimentResult.sentimentAsString());
            jsonObject.put("language_code", languageCode);
            return jsonObject;

        } catch (ComprehendException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public String detectTheDominantLanguage(String text){
        try {
            DetectDominantLanguageRequest request = DetectDominantLanguageRequest.builder()
                .text(text)
                .build();

            CompletableFuture<?> future  = getComprehendAsyncClient().detectDominantLanguage(request);
            future.join();

            DetectDominantLanguageResponse resp = (DetectDominantLanguageResponse) future.join();
            List<DominantLanguage> allLanList = resp.languages();
            if (!allLanList.isEmpty()) {
                DominantLanguage firstLanguage = allLanList.get(0);
                return firstLanguage.languageCode();
            } else {
                return "No languages found";
            }

        } catch (ComprehendException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
