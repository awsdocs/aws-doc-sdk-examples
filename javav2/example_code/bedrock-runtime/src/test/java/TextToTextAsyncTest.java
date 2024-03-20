// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrockruntime.Claude2WithResponseStream;
import com.example.bedrockruntime.InvokeModelAsync;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TextToTextAsyncTest extends TestBase {

    @Test
    @Tag("IntegrationTest")
    void InvokeMistral7BAsync() {
        var prompt = "In one sentence, what is a large-language model?";
        var completions = InvokeModelAsync.invokeMistral7B(prompt);
        assertNotNull(completions);
        assertFalse(completions.isEmpty());
        String result = completions.get(0);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("Test async invoke Mistral 7B passed.");
    }

    @Test
    @Tag("IntegrationTest")
    void InvokeMixtral8x7BAsync() {
        var prompt = "In one sentence, what is a large-language model?";
        var completions = InvokeModelAsync.invokeMixtral8x7B(prompt);
        assertNotNull(completions);
        assertFalse(completions.isEmpty());
        String result = completions.get(0);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("Test async invoke Mixtral 8x7B passed.");
    }

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
    void InvokeClaude2MessagesApiWithResponseStream() {
        var prompt = "In one sentence, what is a large-language model?";
        var responseObject = Claude2WithResponseStream.invokeWithMessagesApi(prompt);
        assertNotNull(responseObject);
        System.out.println("Test invoke Claude 2 with response stream using the Messages API passed.");
    }

    @Test
    @Tag("IntegrationTest")
    void InvokeClaude2TextCompletionsApiWithResponseStream() {
        var prompt = "In one sentence, what is a large-language model?";
        var responseObject = Claude2WithResponseStream.invokeWithTextCompletionsApi(prompt);
        assertNotNull(responseObject);
        System.out.println("Test invoke Claude 2 with response stream using the Messages API passed.");
    }
}
