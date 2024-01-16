// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrockruntime.InvokeModel;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TextToTextSyncTest extends TestBase {

    @Test
    @Tag("IntegrationTest")
    void InvokeClaude() {
        String completion = InvokeModel.invokeClaude("In one sentence, what is a large-language model?");
        assertNotNullOrEmpty(completion);
        System.out.println("Test sync invoke Claude passed.");
    }

    @Test
    @Tag("IntegrationTest")
    void InvokeJurassic2() {
        String completion = InvokeModel.invokeJurassic2("In one sentence, what is a large-language model?");
        assertNotNullOrEmpty(completion);
        System.out.println("Test sync invoke Jurassic-2 passed.");
    }

    @Test
    @Tag("IntegrationTest")
    void InvokeLlama2() {
        String completion = InvokeModel.invokeLlama2("In one sentence, what is a large-language model?");
        assertNotNullOrEmpty(completion);
        System.out.println("Test sync invoke Llama 2 passed.");
    }
}
