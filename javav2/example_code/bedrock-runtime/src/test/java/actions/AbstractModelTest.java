// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import com.example.bedrockruntime.models.anthropicClaude.lib.ReasoningResponse;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractModelTest {

    /**
     * Provide the model classes to test.
     * Each concrete test class must implement this method.
     */
    protected abstract Stream<ModelTest> modelProvider();

    /**
     * Validates the model invocation result isn't empty.
     */
    protected void validateResult(Object result, String modelName) {
        switch (result) {
            case String s -> assertFalse(s.trim().isEmpty(), "Empty result from " + modelName);
            case byte[] b -> assertNotEquals(0, b.length, "Empty result from " + modelName);
            case ReasoningResponse r -> {
                assertFalse(r.reasoning().reasoningText().text().trim().isEmpty(), "No reasoning text from " + modelName);
                assertFalse(r.text().trim().isEmpty(), "No response text from " + modelName);
            }
            case null -> fail("Null result from " + modelName);
            default -> fail("Unexpected result type from " + modelName + ": " + result.getClass());
        }
    }

    @ParameterizedTest(name = "Test {0}")
    @MethodSource("modelProvider")
    void testModel(ModelTest model) {
        Object result = model.methodToCall.get();
        validateResult(result, model.name());
    }

    protected record ModelTest(String name, Supplier<Object> methodToCall) {
    }
}