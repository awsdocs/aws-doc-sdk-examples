// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazonaws.transcribestreaming.BidirectionalStreaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.regions.Region;
import org.junit.jupiter.api.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.*;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assumptions.assumeTrue;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TranscribeTest {
    private static final Logger logger = LoggerFactory.getLogger(TranscribeTest.class);
    private static TranscribeStreamingAsyncClient client;

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {
        Region region = Region.US_EAST_1;
        client = TranscribeStreamingAsyncClient.builder()
                .region(region)
                .build();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void BidirectionalStreaming() throws Exception {
        // Skip this test if no audio input device is available (e.g., CI environments).
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        assumeTrue(AudioSystem.isLineSupported(info), "Skipping: No audio input device available");

        BidirectionalStreaming.convertAudio(client);
        logger.info("\nTest 1 passed");
    }
 }
