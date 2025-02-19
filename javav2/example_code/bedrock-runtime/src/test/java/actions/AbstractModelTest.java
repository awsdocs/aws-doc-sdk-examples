// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
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
     * Provide the method name to test.
     * Each concrete test class must implement this method.
     */
    protected abstract String getMethodName();

    /**
     * Validates the result of the model invocation.
     * Can be overridden by concrete classes if needed.
     */
    protected void validateResult(Object result, String modelName) {
        if (result instanceof String) {
            assertFalse(Objects.requireNonNull((String) result).trim().isEmpty(),
                    "Empty result from " + modelName);
        } else if (result instanceof byte[]) {
            assertNotEquals(0, Objects.requireNonNull((byte[]) result).length,
                    "Empty result from " + modelName);
        } else {
            fail("Unexpected result type from " + modelName + ": " + result.getClass());
        }
    }

    @ParameterizedTest(name = "Test {0}")
    @MethodSource("modelProvider")
    void testModel(ModelTest model) {
        try {
            Object result = model.cls().getMethod(getMethodName()).invoke(null);
            validateResult(result, model.name());

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            fail("Test failed for " + model.name() + ": " + cause.getMessage(), cause);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            fail("Test configuration error for " + model.name() + ": " + e.getMessage(), e);
        }
    }

    protected record ModelTest(String name, Class<?> cls) {
    }
}