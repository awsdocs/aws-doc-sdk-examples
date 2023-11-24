/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.bedrockruntime.InvokeModelWithResponseStream;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Order(1)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TextToTextAsyncTest extends TestBase {

    static private BedrockRuntimeAsyncClient client;

    @BeforeAll()
    static void setUp() {
        client = BedrockRuntimeAsyncClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
                
    }

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    void InvokeModelWithResponseStream() {

        String prompt = "In one sentence, what is a large-language model?";

        assertDoesNotThrow(
            () -> InvokeModelWithResponseStream.invokeModel(client, prompt)
        );

        printSuccessMessage(new Object(){}.getClass().getEnclosingMethod());
    }
}
