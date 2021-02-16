// snippet-sourcedescription:[TranscribeStreamingDemoApp.java transcribes a WAV file. The output is presented on your computer's standard output.]
// snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Transcribe]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[01/26/2021]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.amazonaws.transcribestreaming;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.services.transcribestreaming.model.*;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_UNSIGNED;


public class TranscribeStreamingDemoFile {
    private static final Region REGION = Region.US_EAST_1;
    private static TranscribeStreamingAsyncClient client;

    public static void main(String args[]) throws Exception {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    TranscribeStreamingDemoFile <file> \n\n" +
                "Where:\n" +
                "    file - the location of a WAV file to transcribe. In this example, ensure the WAV file is 16 hertz (Hz). \n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String file = args[0];
        File theFile = new File(file);
        client = TranscribeStreamingAsyncClient.builder()
                .region(REGION)
                .build();

        CompletableFuture<Void> result = client.startStreamTranscription(getRequest(theFile),
                new AudioStreamPublisher(getStreamFromFile(file)),
                getResponseHandler());

        result.get();
        client.close();
    }

    private static InputStream getStreamFromFile(String file) {
        try {
            File inputFile = new File(file);
            InputStream audioStream = new FileInputStream(inputFile);
            return audioStream;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * IMPORTANT: You must know the media encoding and sample hertz rate for your file.  If
     * these values are wrong, then transcribe will not return an error.  It will return an
     * incorrect transcript.
     */
    private static StartStreamTranscriptionRequest getRequest(File inputFile) throws IOException, UnsupportedAudioFileException {
        //Too bad you can't read the input stream twice.  We read the file twice in this example.
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);
        AudioFormat audioFormat = audioInputStream.getFormat();

        return StartStreamTranscriptionRequest.builder()
                .languageCode(LanguageCode.EN_US)
                .mediaEncoding(getAwsMediaEncoding(audioFormat))
                .mediaSampleRateHertz(getAwsSampleRate(audioFormat))
                .build();
    }

    private static StartStreamTranscriptionResponseHandler getResponseHandler() {
        return StartStreamTranscriptionResponseHandler.builder()
                .onResponse(r -> {
                    System.out.println("Received Initial response");
                })
                .onError(e -> {
                    System.out.println(e.getMessage());
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    System.out.println("Error Occurred: " + sw.toString());
                })
                .onComplete(() -> {
                    System.out.println("=== All records stream successfully ===");
                })
                .subscriber(event -> {
                    List<Result> results = ((TranscriptEvent) event).transcript().results();
                    if (results.size() > 0) {
                        if (!results.get(0).alternatives().get(0).transcript().isEmpty()) {
                            System.out.println(results.get(0).alternatives().get(0).transcript());
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

            if (this.currentSubscription == null) {
                this.currentSubscription = new SubscriptionImpl(s, inputStream);
            } else {
                this.currentSubscription.cancel();
                this.currentSubscription = new SubscriptionImpl(s, inputStream);
            }
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
        }

        private ByteBuffer getNextEvent() {
            ByteBuffer audioBuffer = null;
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

    private static MediaEncoding getAwsMediaEncoding(AudioFormat audioFormat) {
        final String javaMediaEncoding = audioFormat.getEncoding().toString();

        //TODO: Add support for MediaEncoding.OGG_OPUS and MediaEncoding.FLAC.
        if (PCM_SIGNED.toString().equals(javaMediaEncoding)) {
            return MediaEncoding.PCM;
        } else if (PCM_UNSIGNED.toString().equals(javaMediaEncoding)){
            return MediaEncoding.PCM;
        } /*else if (ALAW.toString().equals(javaMediaEncoding)){
                return MediaEncoding.OGG_OPUS;
            } else if (ULAW.toString().equals(javaMediaEncoding)){
                return MediaEncoding.FLAC;
            }*/

        throw new IllegalArgumentException("Not a recognized media encoding:" + javaMediaEncoding);
    }

    private static Integer getAwsSampleRate(AudioFormat audioFormat) {
        return Math.round(audioFormat.getSampleRate());
    }
}