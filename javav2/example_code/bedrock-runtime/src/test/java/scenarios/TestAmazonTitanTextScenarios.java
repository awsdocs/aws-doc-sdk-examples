// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios;

import org.junit.jupiter.api.Test;

import static com.example.bedrockruntime.models.amazonTitanText.TextScenarios.invokeWithConversation;
import static com.example.bedrockruntime.models.amazonTitanText.TextScenarios.invokeWithSystemPrompt;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TestAmazonTitanTextScenarios {

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
