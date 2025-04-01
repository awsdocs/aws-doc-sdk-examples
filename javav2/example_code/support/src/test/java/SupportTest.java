// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.support.HelloSupport;
import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;
import com.example.support.SupportScenario;
import software.amazon.awssdk.services.support.SupportClient;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SupportTest {
    private static final Logger logger = LoggerFactory.getLogger(SupportTest.class);
    private static SupportClient supportClient;

    @BeforeAll
    public static void setUp() throws IOException {
        Region region = Region.US_WEST_2;
        supportClient = SupportClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @Test
    @Order(1)
    public void testHelp() {
        assertDoesNotThrow(() -> HelloSupport.displayServices(supportClient));
        logger.info("\n Test 1 passed");
    }
}
