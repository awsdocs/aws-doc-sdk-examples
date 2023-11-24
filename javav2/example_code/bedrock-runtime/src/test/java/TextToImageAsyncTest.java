/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.bedrockruntime.InvokeModelAsync;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TextToImageAsyncTest extends TestBase {

    @Test
    @Tag("IntegrationTest")
    void InvokeStableDiffusion() {
        String prompt = "A sunset over the ocean";
        String stylePreset = "cinematic";
        long seed = 0;
        String base64Result = InvokeModelAsync.invokeStableDiffusion(prompt, seed, stylePreset);
        assertNotNullOrEmpty(base64Result);
        System.out.println("Test async invoke Stable Diffusion passed.");
    }
}