// snippet-sourcedescription:[RetryClient.java is a client that manages the connection to Amazon Transcribe and retries sending data when there are errors on the connection.]
// snippet-service:[transcribe]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Transcribe]
// snippet-keyword:[Code Sample]
// snippet-keyword:[TranscribeStreamingRetryClient]
// snippet-keyword:[TranscribeStreamingAsyncClient]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-10]
// snippet-sourceauthor:[AWS]
// snippet-start:[transcribe.java-streaming-retry-client]
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
 */
package com.amazonaws.transcribestreaming.retryclient;

/**
 * Build a client wrapper around the Amazon Transcribe client to retry 
 * on an exception that can be retried.
 */
public class TranscribeStreamingRetryClient {

    private static final int DEFAULT_MAX_RETRIES = 10;
    private static final int DEFAULT_MAX_SLEEP_TIME_MILLS = 100;
    private int maxRetries = DEFAULT_MAX_RETRIES;
    private int sleepTime = DEFAULT_MAX_SLEEP_TIME_MILLS;
    private final TranscribeStreamingAsyncClient client;
    List<Class<?>> nonRetriableExceptions = Arrays.asList(BadRequestException.class);

    private static final Logger log = LoggerFactory.getLogger(TranscribeStreamingRetryClient.class);
    /**
     * Create a TranscribeStreamingRetryClient with given credential and configuration
     * @param creds Creds to use for transcription
     * @param endpoint Endpoint to use for transcription
     * @param region Region to use for transcriptions
     * @throws URISyntaxException if the endpoint is not a URI
     */
    public TranscribeStreamingRetryClient(AwsCredentialsProvider creds,
                                          String endpoint, Region region) throws URISyntaxException {
        this(TranscribeStreamingAsyncClient.builder()
                .overrideConfiguration(
                        c -> c.putAdvancedOption(
                                SdkAdvancedClientOption.SIGNER,
                                EventStreamAws4Signer.create()))
                .credentialsProvider(creds)
                .endpointOverride(new URI(endpoint))
                .region(region)
                .build());
    }

    /**
     * Initiate TranscribeStreamingRetryClient with TranscribeStreamingAsyncClient
     * @param client TranscribeStreamingAsyncClient
     */
    public TranscribeStreamingRetryClient(TranscribeStreamingAsyncClient client) {
        this.client = client;
    }

    /**
     * Get Max retries
     * @return Max retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Set Max retries
     * @param  maxRetries Max retries
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * Get sleep time
     * @return sleep time between retries
     */
    public int getSleepTime() {
        return sleepTime;
    }

    /**
     * Set sleep time between retries
     * @param sleepTime sleep time
     */
    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * Initiate a Stream Transcription with retry.
     * @param request StartStreamTranscriptionRequest to use to start transcription
     * @param publisher The source audio stream as Publisher
     * @param responseHandler StreamTranscriptionBehavior object that defines how the response needs to be handled.
     * @return Completable future to handle stream response.
     */

    public CompletableFuture<Void> startStreamTranscription(final StartStreamTranscriptionRequest request,
                                                            final Publisher<AudioStream> publisher,
                                                            final StreamTranscriptionBehavior responseHandler) {

        CompletableFuture<Void> finalFuture = new CompletableFuture<>();

        recursiveStartStream(rebuildRequestWithSession(request), publisher, responseHandler, finalFuture, 0);

        return finalFuture;
    }

    /**
     * Recursively call startStreamTranscription() to be called till the request is completed or till we run out of retries.
     * @param request StartStreamTranscriptionRequest
     * @param publisher The source audio stream as Publisher
     * @param responseHandler StreamTranscriptionBehavior object that defines how the response needs to be handled.
     * @param finalFuture final future to finish on completing the chained futures.
     * @param retryAttempt Current attempt number
     */
    private void recursiveStartStream(final StartStreamTranscriptionRequest request,
                                      final Publisher<AudioStream> publisher,
                                      final StreamTranscriptionBehavior responseHandler,
                                      final CompletableFuture<Void> finalFuture,
                                      final int retryAttempt) {
        CompletableFuture<Void> result = client.startStreamTranscription(request, publisher,
                getResponseHandler(responseHandler));
        result.whenComplete((r, e) -> {
            if (e != null) {
                log.debug("Error occured:", e);

                if (retryAttempt <= maxRetries && isExceptionRetriable(e)) {
                    log.debug("Retriable error occurred and will be retried.");
                    log.debug("Sleeping for sometime before retrying...");
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e1) {
                        log.debug("Unable to sleep. Failed with exception: ", e);
                        e1.printStackTrace();
                    }
                    log.debug("Making retry attempt: " + (retryAttempt+1));
                    recursiveStartStream(request, publisher, responseHandler, finalFuture, retryAttempt + 1);
                } else {
                    log.error("Encountered unretriable exception or ran out of retries. ");
                    responseHandler.onError(e);
                    finalFuture.completeExceptionally(e);
                }
            } else {
                responseHandler.onComplete();
                finalFuture.complete(null);
            }
        });
    }
    private StartStreamTranscriptionRequest rebuildRequestWithSession(StartStreamTranscriptionRequest request) {
            return StartStreamTranscriptionRequest.builder()
                    .languageCode(request.languageCode())
                    .mediaEncoding(request.mediaEncoding())
                    .mediaSampleRateHertz(request.mediaSampleRateHertz())
                    .sessionId(UUID.randomUUID().toString())
                    .build();
    }

    /**
     * StartStreamTranscriptionResponseHandler implements subscriber of transcript stream
     * Output is printed to standard output
     */
    private StartStreamTranscriptionResponseHandler getResponseHandler(
            StreamTranscriptionBehavior transcriptionBehavior) {
        final StartStreamTranscriptionResponseHandler build = StartStreamTranscriptionResponseHandler.builder()
                .onResponse(r -> {
                    transcriptionBehavior.onResponse(r);
                })
                .onError(e -> {
                    //Do nothing here. Don't close any streams that shouldn't be cleaned up yet.
                })
                .onComplete(() -> {
                    //Do nothing here. Don't close any streams that shouldn't be cleaned up yet.
                })

                .subscriber(event -> transcriptionBehavior.onStream(event))
                .build();
        return build;
    }

    /**
     * Check if the exception can be retried.
     * @param e Exception that occurred
     * @return True if the exception is retriable
     */
    private boolean isExceptionRetriable(Throwable e) {
        e.printStackTrace();
        if (nonRetriableExceptions.contains(e.getClass())) {
            return false;
        }
        return true;
    }
    public void close() {
        this.client.close();
    }

}
// snippet-end:[transcribe.java-streaming-retry-client]