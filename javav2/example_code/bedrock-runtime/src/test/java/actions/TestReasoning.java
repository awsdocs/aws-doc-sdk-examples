// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestReasoning extends AbstractModelTest {
    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new ModelTest("Claude 3.7 (sync)", com.example.bedrockruntime.models.anthropicClaude.Reasoning::reasoning),
                new ModelTest("Claude 3.7 (async)", com.example.bedrockruntime.models.anthropicClaude.ReasoningAsync::reasoningAsync),
                new ModelTest("Claude 3.7 (stream)", com.example.bedrockruntime.models.anthropicClaude.ReasoningStream::reasoningStream)
        );
    }
}