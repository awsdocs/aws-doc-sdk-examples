// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0v

package models

import java.util.stream.Stream

/**
 * Test class for generative AI models on Amazon Bedrock using the InvokeModel API.
 */
class TestInvokeModel : AbstractModelTest() {
    /**
     * Provides test configurations for generative AI models on Amazon Bedrock.
     * Creates test cases that validate each model's ability to generate
     * and return text or byte[] responses.
     */
    override fun modelProvider(): Stream<ModelTest> = listOf(
        ModelTest("Amazon Titan Text") { com.example.bedrockruntime.models.amazon.titan.text.invokeModel() },
        ModelTest("Amazon Nova Canvas") { com.example.bedrockruntime.models.amazon.nova.canvas.invokeModel() },
    ).stream()
}
