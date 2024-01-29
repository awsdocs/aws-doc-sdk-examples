// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrockruntime.InvokeModelAsync;
import com.example.bedrockruntime.InvokeModelWithResponseStream;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TextToTextAsyncTest extends TestBase {

    @Test
    @Tag("IntegrationTest")
    void InvokeClaudeAsync() {
        var prompt = "In one sentence, what is a large-language model?";
        var generatedText = InvokeModelAsync.invokeClaude(prompt);
        assertNotNullOrEmpty(generatedText);
        System.out.println("Test async invoke Claude passed.");
    }

    @Test
    @Tag("IntegrationTest")
    void InvokeJurassic2Async() {
        var prompt = "In one sentence, what is a large-language model?";
        var generatedText = InvokeModelAsync.invokeJurassic2(prompt);
        assertNotNullOrEmpty(generatedText);
        System.out.println("Test async invoke Jurassic-2 passed.");
    }

    @Test
    @Tag("IntegrationTest")
    void InvokeLlama2Async() {
        var prompt = "In one sentence, what is a large-language model?";
        var generatedText = InvokeModelAsync.invokeLlama2(prompt);
        assertNotNullOrEmpty(generatedText);
        System.out.println("Test async invoke Llama 2 passed.");
    }

    @Test
    @Tag("IntegrationTest")
    void InvokeClaudeWithResponseStream() {
        var prompt = "In one sentence, what is a large-language model?";
        var silent = true;
        var generatedText = InvokeModelWithResponseStream.invokeClaude(prompt, silent);
        assertNotNullOrEmpty(generatedText);
        System.out.println("Test async invoke Claude with response stream passed.");
    }
}
