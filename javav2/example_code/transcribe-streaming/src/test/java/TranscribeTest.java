// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazonaws.transcribestreaming.BidirectionalStreaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.regions.Region;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.URISyntaxException;


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
        BidirectionalStreaming.convertAudio(client);
        logger.info("\nTest 1 passed");
    }
 }
