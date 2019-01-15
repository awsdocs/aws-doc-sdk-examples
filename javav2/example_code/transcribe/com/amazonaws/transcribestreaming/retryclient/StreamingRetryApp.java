// snippet-sourcedescription:[StreamingRetryApp.java is an application that demonstrates using the Amazon Transcribe retry client.]
// snippet-service:[transcribe]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Transcribe]
// snippet-keyword:[Code Sample]
// snippet-keyword:[TranscribeStreamingAsyncClient]
// snippet-keyword:[StartStreamTranscriptionResponse]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-10]
// snippet-sourceauthor:[AWS]
// snippet-start:[transcribe.java-streaming-retry-app]
/**
 * COPYRIGHT:
 *
 * Copyright 2018-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package com.amazonaws.transcribestreaming.retryclient;

public class SampleApp {
    private static final String endpoint = "endpoint";
    private static final Region region = region;
    public static void main(String args[]) throws URISyntaxException, ExecutionException, InterruptedException, LineUnavailableException, FileNotFoundException {
        /**
         * Create Transcribe streaming retry client using AWS credentials.
         */
        TranscribeStreamingRetryClient client = new TranscribeStreamingRetryClient(getCredentials(), endpoint, region);

        StartStreamTranscriptionRequest request =  StartStreamTranscriptionRequest.builder()
                .languageCode(LanguageCode.language.toString())
                .mediaEncoding(encoding)
                .mediaSampleRateHertz(sample rate)
                .build();
        /**
         * Start real-time speech recognition. The Transcribe streaming java client uses the Reactive-streams 
         * interface. For reference on Reactive-streams: 
         *     https://github.com/reactive-streams/reactive-streams-jvm
         */
        CompletableFuture<Void> result = client.startStreamTranscription(
                /**
                 * Request parameters. Refer to API documentation for details.
                 */
                request,
                /**
                 * Provide an input audio stream.
                 * For input from a microphone, use getStreamFromMic().
                 * For input from a file, use getStreamFromFile().
                 */
                new AudioStreamPublisher(
                        new FileInputStream(new File("FileName"))),
                /**
                 * Object that defines the behavior on how to handle the stream
                 */
                new StreamTranscriptionBehaviorImpl());

        /**
         * Synchronous wait for stream to close, and close client connection
         */
        result.get();
        client.close();
    }
    
    private static class AudioStreamPublisher implements Publisher<AudioStream> {
    private final InputStream inputStream;

        private AudioStreamPublisher(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void subscribe(Subscriber<? super AudioStream> s) {
            if (currentSubscription == null) {
                this.currentSubscription = new SubscriptionImpl(s, inputStream);
            } else {
                this.currentSubscription.cancel();
                this.currentSubscription = new SubscriptionImpl(s, inputStream);
            }
            s.onSubscribe(currentSubscription);
        }
    }
}
// snippet-end:[transcribe.java-streaming-retry-app]