// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.example.polly.DescribeVoicesSample;
import com.example.polly.ListLexicons;
import com.example.polly.PollyDemo;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import java.io.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class AWSPollyTest {
    private static PollyClient polly;
    private static final Logger logger = LoggerFactory.getLogger(AWSPollyTest.class);
    @BeforeAll
    public static void setUp() {
        polly = PollyClient.builder()
            .region(Region.US_WEST_2)
            .build();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testDescribeVoicesSample() {
        assertDoesNotThrow(() ->DescribeVoicesSample.describeVoice(polly));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testListLexicons() {
        assertDoesNotThrow(() ->ListLexicons.listLexicons(polly));
        logger.info("Test 2 passed");
    }
}
