/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.bedrockruntime.InvokeModel;
import com.example.bedrockruntime.InvokeModelWithResponseStream;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BedrockRuntimeTest {

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    public void InvokeModel() {

        try (BedrockRuntimeClient bedrockRuntime = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {

            String prompt = "In one sentence, what is a large-language model?";

            String completion = InvokeModel.invokeModel(bedrockRuntime, prompt);

            assertNotNull(completion, "The completion is null");
            assertFalse(completion.trim().isEmpty(), "The completion is empty");

            System.out.println("Test 1 passed.");
        }
    }

    @Test
    @Order(2)
    @Tag("IntegrationTest")
    public void InvokeModelWithResponseStream() {

        try (BedrockRuntimeAsyncClient bedrockRuntime = BedrockRuntimeAsyncClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {

            String prompt = "In one sentence, what is a large-language model?";

            assertDoesNotThrow(() -> InvokeModelWithResponseStream.invokeModel(bedrockRuntime, prompt));

            System.out.println("Test 2 passed.");
        }
    }
}
