// snippet-sourcedescription:[StreamTranscriptionBehavior.java is an interface that you implement for streaming transcription.]
// snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Transcribe]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/06/2020]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
// snippet-start:[transcribe.java-streaming-client-behavior]
package com.amazonaws.transcribestreaming;

import software.amazon.awssdk.services.transcribestreaming.model.StartStreamTranscriptionResponse;
import software.amazon.awssdk.services.transcribestreaming.model.TranscriptResultStream;

/**
 * Defines how a stream response should be handled.
 * You should build a class implementing this interface to define the behavior.
 */
public interface StreamTranscriptionBehavior {
    /**
     * Defines how to respond when encountering an error on the stream transcription.
     */
    void onError(Throwable e);

    /**
     * Defines how to respond to the Transcript result stream.
     */
    void onStream(TranscriptResultStream e);

    /**
     * Defines what to do on initiating a stream connection with the service.
     */
    void onResponse(StartStreamTranscriptionResponse r);


    /**
     * Defines what to do on stream completion
     */
    void onComplete();
}
// snippet-end:[transcribe.java-streaming-client-behavior]