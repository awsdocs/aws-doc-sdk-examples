/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.bedrockruntime.InvokeModel;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TextToImageSyncTest extends TestBase {

    @Test
    @Tag("IntegrationTest")
    void InvokeStableDiffusion() {
        String prompt = "A sunset over the ocean";
        String base64Result = InvokeModel.invokeStableDiffusion(prompt, 0, "cinematic");
        assertNotNullOrEmpty(base64Result);
        System.out.println("Test sync invoke Stable Diffusion passed.");
    }
}