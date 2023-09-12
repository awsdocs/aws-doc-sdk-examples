/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateException;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

public class TranslateService {

    private static TranslateClient translateClient;

    private static synchronized TranslateClient getTranslateClient() {
        if (translateClient == null) {
            translateClient = TranslateClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return translateClient;
    }

    public String translateText(String lanCode, String text) {
        try {
            TranslateTextRequest textRequest = TranslateTextRequest.builder()
                .sourceLanguageCode(lanCode)
                .targetLanguageCode("en")
                .text(text)
                .build();

            TranslateTextResponse textResponse = getTranslateClient().translateText(textRequest);
            return textResponse.translatedText();

        } catch (TranslateException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
}
