/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.PollyException;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.Voice;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import java.io.IOException;
import java.io.InputStream;

public class PollyService {
    private static PollyClient pollyClientInstance;

    private static synchronized PollyClient getPollyClient() {
        if (pollyClientInstance == null) {
            Region region = Region.US_EAST_1;
            pollyClientInstance = PollyClient.builder()
                .region(region)
                .build();
        }
        return pollyClientInstance;
    }

    public InputStream synthesize(String text) throws IOException {
        try {
            DescribeVoicesRequest describeVoicesRequest = DescribeVoicesRequest.builder()
                .engine("neural")
                .build();

            DescribeVoicesResponse describeVoicesResult = getPollyClient().describeVoices(describeVoicesRequest);
            Voice voice = describeVoicesResult.voices().stream()
                .filter(v -> v.name().equals("Joanna"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Voice not found"));

            SynthesizeSpeechRequest synthReq = SynthesizeSpeechRequest.builder()
                .text(text)
                .outputFormat(OutputFormat.MP3)
                .voiceId(voice.id())
                .build();

            return getPollyClient().synthesizeSpeech(synthReq);

        } catch (PollyException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return null;
    }
}
