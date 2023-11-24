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
class TextToImageTest extends TestBase {

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
    void InvokeStableDiffusion() {

        String prompt = "A sunset over the ocean";
        String stylePreset = "cinematic";
        long seed = 0;

        String base64Result = InvokeModel.invokeStableDiffusion(client, prompt, seed, stylePreset);

        assertNotNullOrEmpty(base64Result);

        printSuccessMessage(new Object(){}.getClass().getEnclosingMethod());
    }
}