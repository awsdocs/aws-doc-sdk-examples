/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.bedrockruntime.InvokeModel;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TextToTextSyncTest extends TestBase {

    static private BedrockRuntimeClient client;

    @BeforeAll()
    static void setUp() {
        client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
                
    }

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    void InvokeClaude() {

        String prompt = "In one sentence, what is a large-language model?";

        String completion = InvokeModel.invokeClaude(client, prompt);

        assertNotNullOrEmpty(completion);

        printSuccessMessage(new Object(){}.getClass().getEnclosingMethod());
    }

    @Test
    @Order(2)
    @Tag("IntegrationTest")
    void InvokeJurassic2() {

        String prompt = "In one sentence, what is a large-language model?";

        String completion = InvokeModel.invokeJurassic2(client, prompt);

        assertNotNullOrEmpty(completion);

        printSuccessMessage(new Object(){}.getClass().getEnclosingMethod());
    }

    @Test
    @Order(3)
    @Tag("IntegrationTest")
    void InvokeLlama2() {

        String prompt = "In one sentence, what is a large-language model?";

        String completion = InvokeModel.invokeLlama2(client, prompt);

        assertNotNullOrEmpty(completion);

        printSuccessMessage(new Object(){}.getClass().getEnclosingMethod());
    }
}
