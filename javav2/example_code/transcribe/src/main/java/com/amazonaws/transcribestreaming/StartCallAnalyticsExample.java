// snippet-sourcedescription:[StartCallAnalyticsExample.java transcribes streaming audio from your computer's microphone. The output is presented on your computer's standard output.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Transcribe]

package com.amazonaws.transcribestreaming;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.transcribe.model.LanguageCode;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.services.transcribestreaming.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sound.sampled.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

// snippet-start:[transcribe.java-streaming-call-demo]
/**
 * To run this AWS code example, ensure that you have set up your development environment, including your AWS credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class  StartCallAnalyticsExample {

    private static final Logger logger = LoggerFactory.getLogger(StartCallAnalyticsExample.class);
    private static TranscribeStreamingAsyncClient transcribeAsyncClient;

    public static void main(String[] args) {
        try {
            Region region = Region.US_WEST_2;
            try (TranscribeStreamingAsyncClient transcribeAsyncClient = TranscribeStreamingAsyncClient.builder()
                .region(region)
                .build();
                 TargetDataLine line = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, getAudioFormat()))) {

                StartCallAnalyticsStreamTranscriptionRequest request = StartCallAnalyticsStreamTranscriptionRequest.builder()
                    .languageCode(String.valueOf(LanguageCode.EN_US))
                    .mediaSampleRateHertz(16_000)
                    .mediaEncoding(MediaEncoding.FLAC)
                    .build();

                CompletableFuture<Void> result = transcribeAsyncClient.startCallAnalyticsStreamTranscription(
                    request, new AudioStreamPublisher(getStreamFromMic(line)), getResponseHandler());

                result.get();
            }
        } catch (Exception e) {
            logger.error("An error occurred:", e);
        } finally {
            if (transcribeAsyncClient != null) {
                transcribeAsyncClient.close();
            }
        }
    }

    private static AudioFormat getAudioFormat() {
        int sampleRate = 16000;
        return new AudioFormat(sampleRate, 16, 1, true, false);
    }

    private static InputStream getStreamFromMic(TargetDataLine line) {
        line.start();
        return new AudioInputStream(line);
    }

    private static StartCallAnalyticsStreamTranscriptionResponseHandler getResponseHandler() {
        return StartCallAnalyticsStreamTranscriptionResponseHandler.builder()
            .onResponse(r -> logger.info("Received Initial response"))
            .onError(e -> {
                logger.error(e.getMessage());
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error("Error Occurred: {}", sw.toString());
            })
            .onComplete(() -> logger.info("=== All records streamed successfully ==="))
            .subscriber(event -> {
                List<Result> results = ((TranscriptEvent) event).transcript().results();
                if (!results.isEmpty()) {
                    String transcript = results.get(0).alternatives().get(0).transcript();
                    if (!transcript.isEmpty()) {
                        logger.info(transcript);
                    }
                }
            })
            .build();
    }

    private static class AudioStreamPublisher implements Publisher<AudioStream> {
        private final InputStream inputStream;
        private static Subscription currentSubscription;

        private AudioStreamPublisher(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void subscribe(Subscriber<? super AudioStream> s) {
            if (currentSubscription != null) {
                currentSubscription.cancel();
            }
            currentSubscription = new SubscriptionImpl(s, inputStream);
            s.onSubscribe(currentSubscription);
        }
    }

    public static class SubscriptionImpl implements Subscription {
        private static final int CHUNK_SIZE_IN_BYTES = 1024 * 1;
        private final Subscriber<? super AudioStream> subscriber;
        private final InputStream inputStream;
        private ExecutorService executor = Executors.newFixedThreadPool(1);
        private AtomicLong demand = new AtomicLong(0);

        SubscriptionImpl(Subscriber<? super AudioStream> s, InputStream inputStream) {
            this.subscriber = s;
            this.inputStream = inputStream;
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                subscriber.onError(new IllegalArgumentException("Demand must be positive"));
            }

            demand.getAndAdd(n);
            executor.submit(() -> {
                try {
                    do {
                        ByteBuffer audioBuffer = getNextEvent();
                        if (audioBuffer.remaining() > 0) {
                            AudioEvent audioEvent = audioEventFromBuffer(audioBuffer);
                            subscriber.onNext(audioEvent);
                        } else {
                            subscriber.onComplete();
                            break;
                        }
                    } while (demand.decrementAndGet() > 0);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            });
        }

        @Override
        public void cancel() {
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Handle the interruption if needed
            }
        }

        private ByteBuffer getNextEvent() {
            ByteBuffer audioBuffer;
            byte[] audioBytes = new byte[CHUNK_SIZE_IN_BYTES];

            int len = 0;
            try {
                len = inputStream.read(audioBytes);

                if (len <= 0) {
                    audioBuffer = ByteBuffer.allocate(0);
                } else {
                    audioBuffer = ByteBuffer.wrap(audioBytes, 0, len);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            return audioBuffer;
        }

        private AudioEvent audioEventFromBuffer(ByteBuffer bb) {
            return AudioEvent.builder()
                .audioChunk(SdkBytes.fromByteBuffer(bb))
                .build();
        }
    }
}
// snippet-end:[transcribe.java-streaming-call-demo]