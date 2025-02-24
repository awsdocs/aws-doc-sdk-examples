// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package models

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * Abstract base class for testing Bedrock model invocations.
 *
 * Example usage:
 * ```
 * class TestMyModel : AbstractModelTest() {
 *     override fun modelProvider(): Stream<ModelTest> {
 *         return listOf(
 *             ModelTest("My Model", ::myModelFunction)
 *         ).stream()
 *     }
 * }
 * ```
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractModelTest {

    /**
     * Provides the model test configurations to be executed.
     * Implementing classes should override this to return a stream of [ModelTest] instances,
     * each representing a specific model function to be tested.
     *
     * @return A stream of [ModelTest] configurations
     */
    protected abstract fun modelProvider(): Stream<ModelTest>

    /**
     * Executes the test for a given model configuration.
     * This method runs the invocation function and validates its output using [validateResult].
     *
     * @param model The [ModelTest] configuration to execute
     */
    @ParameterizedTest(name = "Test {0}")
    @MethodSource("modelProvider")
    fun testModel(model: ModelTest) = runBlocking {
        try {
            val result = model.function.invoke()
            validateResult(result, model.name)
        } catch (e: Exception) {
            fail("Test failed for ${model.name}: ${e.message}", e)
        }
    }

    /**
     * Validates the result returned by a model invocation.
     * Default implementation ensures that String results are non-empty and ByteArray results have non-zero length.
     * Subclasses can override this to implement custom validation logic.
     *
     * @param result The result returned by the model invocation
     * @param modelName The name of the model being tested
     * @throws AssertionError if the result is invalid
     */
    protected open fun validateResult(result: Any?, modelName: String) {
        when (result) {
            is String -> assertFalse(result.trim().isEmpty()) { "Empty result from $modelName" }
            is ByteArray -> assertNotEquals(0, result.size) { "Empty result from $modelName" }
            else -> fail("Unexpected result type from $modelName: ${result?.javaClass}")
        }
    }

    /**
     * Data class representing a model test configuration.
     * Encapsulates a model invocation function and its descriptive name.
     *
     * @property name The descriptive name of the model being tested
     * @property function The suspend function to be tested, which should return a String
     */
    data class ModelTest(
        val name: String,
        val function: suspend () -> String
    )
}
