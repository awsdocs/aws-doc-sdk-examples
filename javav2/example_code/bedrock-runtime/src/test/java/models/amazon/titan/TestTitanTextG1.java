// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package models.amazon.titan;

import com.example.bedrockruntime.models.amazon.titan.TextG1Quickstart;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static com.example.bedrockruntime.models.amazon.titan.TextG1Scenarios.invokeWithConversation;
import static com.example.bedrockruntime.models.amazon.titan.TextG1Scenarios.invokeWithSystemPrompt;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("IntegrationTest")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestTitanTextG1 {
    @Test
    void quickstart() {
        assertDoesNotThrow(() -> TextG1Quickstart.main(null));
    }

    @Test
    void invokeWithSystemPromptScenario() {
        var inputText = "Hello, how are you today?";
        var systemPrompt = "Only respond with 'OK'";
        var response = invokeWithSystemPrompt(inputText, systemPrompt);
        assertFalse(response.getJSONArray("results").isEmpty());
    }

    @Test
    void invokeWithConversationScenario() {
        var conversation = """
                User: Hello, how are you today?
                Bot: OK
                """;

        var inputText = "What was my first question? Respond with 'Your question was \"[QUESTION]\n'";

        var response = invokeWithConversation(inputText, conversation);
        assertFalse(response.getJSONArray("results").isEmpty());
    }
}
