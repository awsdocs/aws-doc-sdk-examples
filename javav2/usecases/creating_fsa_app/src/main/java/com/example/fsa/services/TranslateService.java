/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateAsyncClient;
import software.amazon.awssdk.services.translate.model.TranslateException;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.util.concurrent.CompletableFuture;

public class TranslateService {

    private static TranslateAsyncClient translateAsyncClient;

    private static synchronized TranslateAsyncClient getTranslateAsyncClient() {
        if (translateAsyncClient == null) {
            translateAsyncClient = TranslateAsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return translateAsyncClient;
    }

    public String translateText(String lanCode, String text) {
        try {
            TranslateTextRequest textRequest = TranslateTextRequest.builder()
                .sourceLanguageCode(lanCode)
                .targetLanguageCode("en")
                .text(text)
                .build();

            CompletableFuture<?> future = getTranslateAsyncClient().translateText(textRequest);
            TranslateTextResponse textResponse = (TranslateTextResponse) future.join();
            return textResponse.translatedText();

        } catch (TranslateException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
}
