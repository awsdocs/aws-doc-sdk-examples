// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.support.HelloSupport;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;
import com.example.support.SupportScenario;
import software.amazon.awssdk.services.support.SupportClient;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SupportTest {

    private static SupportClient supportClient;
    private static String fileAttachment = "";

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
    public void helloTest() {
        HelloSupport.displayServices(supportClient);
    }
}
