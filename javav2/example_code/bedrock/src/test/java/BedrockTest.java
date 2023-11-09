/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.bedrock.ListFoundationModels;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class BedrockTest {

    private static BedrockClient bedrock;

    @BeforeAll
    public static void setUp() throws IOException {
        bedrock = BedrockClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @Test
    @Tag("IntegrationTest")
    public void ListFoundationModels() {
        assertDoesNotThrow(() -> ListFoundationModels.listFoundationModels(bedrock));
        System.out.println("Test 1 passed.");
    }
}
