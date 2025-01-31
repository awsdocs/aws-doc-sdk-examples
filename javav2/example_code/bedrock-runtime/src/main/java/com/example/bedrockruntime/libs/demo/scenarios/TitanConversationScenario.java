// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs.demo.scenarios;

import com.example.bedrockruntime.libs.demo.DemoRunner.DemoState;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.BiFunction;

public class TitanConversationScenario extends ConversationScenario {
    public TitanConversationScenario(BiFunction<String, String, JSONObject> action) {
        super(action);
    }

    @Override
    public void run(DemoState state) throws IOException {
        var conversation = "User: %s\nBot: %s" .formatted(
                state.messages.getJSONObject(0).getString("text"),
                state.messages.getJSONObject(1).getString("text"));
        super.runWith(conversation);
    }

}
