/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

public class TranslateService {

    public String translateText(String lanCode, String text) {
        TranslateClient translateClient = TranslateClient.builder()
            .region(Region.US_EAST_1)
            .build();

        TranslateTextRequest textRequest = TranslateTextRequest.builder()
            .sourceLanguageCode(lanCode)
            .targetLanguageCode("en")
            .text(text)
            .build();

        TranslateTextResponse textResponse = translateClient.translateText(textRequest);
        return textResponse.translatedText();
    }
}
