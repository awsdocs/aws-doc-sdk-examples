/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.bedrockruntime.InvokeModel;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BedrockRuntimeTest {

    private static BedrockRuntimeClient bedrockRuntime;

    @BeforeAll
    public static void setUp() throws IOException {
        bedrockRuntime = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    public void InvokeModel() {

        String prompt = "In one sentence, what is a large-language model?";

        String completion = InvokeModel.invokeModel(bedrockRuntime, prompt);

        assertNotNull(completion, "The completion is null");
        assertFalse(completion.trim().isEmpty(), "The completion is empty");

        System.out.println("Test 1 passed.");
    }
}
