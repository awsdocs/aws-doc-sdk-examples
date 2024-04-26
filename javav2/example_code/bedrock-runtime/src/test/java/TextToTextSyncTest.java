// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrockruntime.InvokeModel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TextToTextSyncTest extends TestBase {

    @Test
    @Tag("IntegrationTest")
    void InvokeMistral7B() {
        var prompt = "In one sentence, what is a large-language model?";
        var completions = InvokeModel.invokeMistral7B(prompt);
        assertNotNull(completions);
        assertFalse(completions.isEmpty());
        var result = completions.get(0);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("Test sync invoke Mistral 7B passed.");
    }

    @Test
    @Tag("IntegrationTest")
    void InvokeMixtral8x7B() {
        var prompt = "In one sentence, what is a large-language model?";
        var completions = InvokeModel.invokeMixtral8x7B(prompt);
        assertNotNull(completions);
        assertFalse(completions.isEmpty());
        var result = completions.get(0);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("Test sync invoke Mixtral 8x7B passed.");
    }

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
}
