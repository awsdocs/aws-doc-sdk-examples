// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs.demo.scenarios;

import com.example.bedrockruntime.libs.demo.DemoRunner.DemoState;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.BiFunction;

import static com.example.bedrockruntime.libs.demo.Utils.printResponse;

public class SystemPromptScenario extends Scenario {
    private JSONObject response;
    private String userPrompt;

    public SystemPromptScenario(BiFunction<String, String, JSONObject> action) {
        super(action, "How to add a system prompt and additional parameters");
    }

    @Override
    public void run(DemoState demoState) throws IOException {
        userPrompt = "Write a haiku about a sunset.";
        var systemPrompt = "Your response must contain the word 'developer'.";

        System.out.printf("User prompt:   \"%s\"%n", userPrompt);
        System.out.printf("System prompt: \"%s\"%n%n", systemPrompt);

        System.out.println(WAITING_FOR_RESPONSE);

        if (action instanceof BiFunction<?, ?, ?>) {
            response = ((BiFunction<String, String, JSONObject>) action).apply(userPrompt, systemPrompt);
        } else {
            throw new IllegalArgumentException("Error: The action is of an invalid type.");
        }

        printResponse(response);
        update(demoState);
    }

    void update(DemoState demoState) {
        var responseText = response.getJSONArray("results").getJSONObject(0).getString("outputText");
        demoState.messages.put(new JSONObject().put("role", "user").put("text", userPrompt));
        demoState.messages.put(new JSONObject().put("role", "assistant").put("text", responseText));
    }
}
