// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestConverseAsync extends AbstractModelTest {
    protected String getMethodName() {
        return "converseAsync";
    }

    protected Stream<TestConverseAsync.ModelTest> modelProvider() {
        return Stream.of(
                new TestConverseAsync.ModelTest("Jurassic2", com.example.bedrockruntime.models.ai21LabsJurassic2.ConverseAsync.class),
                new TestConverseAsync.ModelTest("NovaText", com.example.bedrockruntime.models.amazon.nova.text.ConverseAsync.class),
                new TestConverseAsync.ModelTest("TitanText", com.example.bedrockruntime.models.amazonTitanText.ConverseAsync.class),
                new TestConverseAsync.ModelTest("Claude", com.example.bedrockruntime.models.anthropicClaude.ConverseAsync.class),
                new TestConverseAsync.ModelTest("CohereCommand", com.example.bedrockruntime.models.cohereCommand.ConverseAsync.class),
                new TestConverseAsync.ModelTest("Mistral", com.example.bedrockruntime.models.mistral.ConverseAsync.class)
        );
    }
}