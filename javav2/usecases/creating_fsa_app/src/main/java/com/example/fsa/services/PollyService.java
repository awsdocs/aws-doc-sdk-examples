/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa.services;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyAsyncClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.PollyException;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechResponse;
import software.amazon.awssdk.services.polly.model.Voice;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class PollyService {
    private static PollyAsyncClient pollyAsyncClient;

    private static synchronized PollyAsyncClient getPollyAsyncClient() {
        if (pollyAsyncClient == null) {
            Region region = Region.US_EAST_1;
            pollyAsyncClient = PollyAsyncClient.builder()
                .region(region)
                .build();
        }
        return pollyAsyncClient;
    }

    public InputStream synthesize(String text) throws IOException {
        try {
            DescribeVoicesRequest describeVoicesRequest = DescribeVoicesRequest.builder()
                .engine("neural")
                .build();

            CompletableFuture<?> future  = getPollyAsyncClient().describeVoices(describeVoicesRequest);
            DescribeVoicesResponse describeVoicesResult = (DescribeVoicesResponse) future.join();
            Voice voice = describeVoicesResult.voices().stream()
                .filter(v -> v.name().equals("Joanna"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Voice not found"));

            SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                .text(text)
                .outputFormat(OutputFormat.MP3)
                .voiceId(voice.id())
                .build();

            CompletableFuture<ResponseInputStream<SynthesizeSpeechResponse>> audioFuture = getPollyAsyncClient().synthesizeSpeech(request, AsyncResponseTransformer.toBlockingInputStream());
            InputStream audioInputStream = audioFuture.join();
            return audioInputStream;

        } catch (PollyException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
}
