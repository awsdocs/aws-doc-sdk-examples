// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package models

import java.util.stream.Stream

/**
 * Test class for text generation on Amazon Bedrock using the Converse API.
 */
class TestConverse : AbstractModelTest() {
    /**
     * Provides test configurations for Amazon Bedrock text generation models.
     * Creates test cases that validate each model's ability to generate
     * and return text responses.
     */
    override fun modelProvider(): Stream<ModelTest> = listOf(
        ModelTest("Amazon Nova") { com.example.bedrockruntime.models.amazon.nova.text.converse() },
    ).stream()
}
