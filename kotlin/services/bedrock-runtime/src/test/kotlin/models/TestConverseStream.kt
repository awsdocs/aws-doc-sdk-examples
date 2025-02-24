// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package models

import java.util.stream.Stream

/**
 * Test class for streaming text generation on Amazon Bedrock using the ConverseStream API.
 */
class TestConverseStream : AbstractModelTest() {
    /**
     * Provides test configurations for Amazon Bedrock models that support streaming.
     * Creates test cases that validate each model's ability to generate
     * and return streaming text responses.
     */
    override fun modelProvider(): Stream<ModelTest> {
        return listOf(
            ModelTest("Amazon Nova") { com.example.bedrockruntime.models.amazon.nova.text.converseStream() }
        ).stream()
    }
}