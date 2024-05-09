// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs.demo.scenarios;

import com.example.bedrockruntime.libs.demo.DemoRunner.DemoState;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.BiFunction;

import static com.example.bedrockruntime.libs.demo.Utils.printResponse;

public abstract class ConversationScenario extends Scenario {

    public ConversationScenario(BiFunction<String, String, JSONObject> action) {
        super(action, "How to use a conversation history to simulate a chat");
    }

    public abstract void run(DemoState state) throws IOException;

    protected void runWith(String conversation) throws IOException {
        var userPrompt = "Take the role of a poetry expert and explain the Haiku above.";
        System.out.printf("User prompt: \"%s\"%n", userPrompt);
        System.out.println("Conversation history:");
        System.out.println(conversation + "\n");

        System.out.println(WAITING_FOR_RESPONSE);

        if (action instanceof BiFunction<?, ?, ?>) {
            JSONObject response = ((BiFunction<String, String, JSONObject>) action).apply(userPrompt, conversation);
            printResponse(response);
        } else {
            throw new IllegalArgumentException("Error: The action is of an invalid type.");
        }
    }
}
