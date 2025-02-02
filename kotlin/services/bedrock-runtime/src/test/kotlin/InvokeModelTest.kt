// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrockruntime.invokeModel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class InvokeModelTest {
    @Test
    @Order(1)
    fun listFoundationModels() = runBlocking {
        val prompt = "What is the capital of France?"

        val answer = invokeModel(prompt, "amazon.titan-text-lite-v1")
        assertTrue(answer.isNotBlank())
    }
}
