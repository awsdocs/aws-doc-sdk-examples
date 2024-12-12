// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazonaws.transcribestreaming;

import software.amazon.awssdk.services.transcribestreaming.model.Result;
import software.amazon.awssdk.services.transcribestreaming.model.StartStreamTranscriptionResponse;
import software.amazon.awssdk.services.transcribestreaming.model.TranscriptEvent;
import software.amazon.awssdk.services.transcribestreaming.model.TranscriptResultStream;
import java.util.List;

// snippet-start:[transcribe.java-streaming-client-behavior-imp]
public class StreamTranscriptionBehaviorImpl implements StreamTranscriptionBehavior {

    @Override
    public void onError(Throwable e) {
        System.out.println("=== Failure encountered ===");
        e.printStackTrace();
    }

    @Override
    public void onStream(TranscriptResultStream e) {
        List<Result> results = ((TranscriptEvent) e).transcript().results();
        if (results.size() > 0) {
            if (results.get(0).alternatives().size() > 0)
                if (!results.get(0).alternatives().get(0).transcript().isEmpty()) {
                    System.out.println(results.get(0).alternatives().get(0).transcript());
                }
        }
    }

    @Override
    public void onResponse(StartStreamTranscriptionResponse r) {

        System.out.println(String.format("=== Received initial response. Request Id: %s ===", r.requestId()));
    }

    @Override
    public void onComplete() {
        System.out.println("=== All records streamed successfully ===");
    }
}
// snippet-end:[transcribe.java-streaming-client-behavior-imp]
