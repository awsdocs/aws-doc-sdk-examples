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

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BedrockRuntimeTest {

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    void InvokeClaude() {

        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {

            String prompt = "In one sentence, what is a large-language model?";

            String completion = InvokeModel.invokeClaude(client, prompt);

            assertNotNull(completion, "The completion is null");
            assertFalse(completion.trim().isEmpty(), "The completion is empty");

            System.out.printf("Test %d passed.%n", getTestNumber(new Object(){}.getClass().getEnclosingMethod()));
        }
    }

    @Test
    @Order(2)
    @Tag("IntegrationTest")
    void InvokeJurassic2() {

        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {

            String prompt = "In one sentence, what is a large-language model?";

            String completion = InvokeModel.invokeJurassic2(client, prompt);

            assertNotNull(completion, "The completion is null");
            assertFalse(completion.trim().isEmpty(), "The completion is empty");

            System.out.printf("Test %d passed.%n", getTestNumber(new Object(){}.getClass().getEnclosingMethod()));
        }
    }

    @Test
    @Order(3)
    @Tag("IntegrationTest")
    void InvokeLlama2() {

        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {

            String prompt = "In one sentence, what is a large-language model?";

            String completion = InvokeModel.invokeLlama2(client, prompt);

            assertNotNull(completion, "The completion is null");
            assertFalse(completion.trim().isEmpty(), "The completion is empty");

            System.out.printf("Test %d passed.%n", getTestNumber(new Object(){}.getClass().getEnclosingMethod()));
        }
    }

    @Test
    @Order(4)
    @Tag("IntegrationTest")
    void InvokeStableDiffusion() {

        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {

            String prompt = "A sunset over the ocean";
            String stylePreset = "cinematic";
            long seed = (0);

            String result = InvokeModel.invokeStableDiffusion(client, prompt, seed, stylePreset);

            assertNotNull(result, "The result is null");
            assertFalse(result.trim().isEmpty(), "The result is empty");

            System.out.printf("Test %d passed.%n", getTestNumber(new Object(){}.getClass().getEnclosingMethod()));
        }
    }

    @Test
    @Order(5)
    @Tag("IntegrationTest")
    void InvokeModelWithResponseStream() {

        try (BedrockRuntimeAsyncClient bedrockRuntime = BedrockRuntimeAsyncClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {

            String prompt = "In one sentence, what is a large-language model?";

            assertDoesNotThrow(() -> InvokeModelWithResponseStream.invokeModel(bedrockRuntime, prompt));

            System.out.printf("Test %d passed.%n", getTestNumber(new Object(){}.getClass().getEnclosingMethod()));
        }
    }

    private int getTestNumber(Method testMethod) {
        Order order = testMethod.getAnnotation(Order.class);
        return order != null ? order.value() : 0;
    }
}
