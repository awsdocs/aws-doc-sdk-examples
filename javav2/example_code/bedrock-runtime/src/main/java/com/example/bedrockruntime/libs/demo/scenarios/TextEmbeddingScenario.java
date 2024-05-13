// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs.demo.scenarios;

import com.example.bedrockruntime.libs.TriFunction;
import com.example.bedrockruntime.libs.demo.DemoRunner.DemoState;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Function;

import static com.example.bedrockruntime.libs.demo.Utils.printResponse;

public class TextEmbeddingScenario extends Scenario {

    public static final String TITLE = "How to create an embedding with custom inference parameters";

    public TextEmbeddingScenario(TriFunction<String, Integer, Boolean, JSONObject> action) {
        super(action, TITLE);
    }

    public TextEmbeddingScenario(Function<String, JSONObject> action) {
        super(action, TITLE);
    }

    @Override
    public void run(DemoState demoState) throws IOException {
        var inputText = "Please recommend books with a theme similar to the movie 'Inception'.";
        System.out.printf("%nInput text: \"%s\"%n%n", inputText);

        if (action instanceof TriFunction<?, ?, ?, ?>) {
            printResponse(runV2((TriFunction<String, Integer, Boolean, JSONObject>) action, inputText));
        } else if (action instanceof Function<?, ?>) {
            printResponse(runG1((Function<String, JSONObject>) action, inputText));
        } else {
            throw new IllegalArgumentException("Error: The action is of an invalid type.");
        }
    }

    private JSONObject runG1(Function<String, JSONObject> func, String inputText) {
        System.out.println(WAITING_FOR_RESPONSE);
        return func.apply(inputText);
    }

    private JSONObject runV2(TriFunction<String, Integer, Boolean, JSONObject> func, String inputText) {
        var dimensions = 256;
        var normalize = true;
        System.out.printf("Dimensions: '%d'%n", dimensions);
        System.out.printf("Normalize: '%b'%n%n", normalize);
        System.out.println(WAITING_FOR_RESPONSE);
        return func.apply(inputText, dimensions, normalize);
    }
}
