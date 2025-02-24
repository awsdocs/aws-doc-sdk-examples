// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package models

import com.example.bedrockruntime.models.amazon.nova.text.converse

import java.util.stream.Stream

class TestConverse : AbstractModelTest() {
    override fun modelProvider(): Stream<ModelTest> {
        return listOf(
            ModelTest("Amazon Nova", ::converse)
        ).stream()
    }
}