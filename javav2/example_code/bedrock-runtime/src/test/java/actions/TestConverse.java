// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestConverse extends AbstractModelTest {
    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new ModelTest("Claude", com.example.bedrockruntime.models.anthropicClaude.Converse::converse),
                new ModelTest("CohereCommand", com.example.bedrockruntime.models.cohereCommand.Converse::converse),
                new ModelTest("Jurassic2", com.example.bedrockruntime.models.ai21LabsJurassic2.Converse::converse),
                new ModelTest("Mistral", com.example.bedrockruntime.models.mistral.Converse::converse),
                new ModelTest("NovaText", com.example.bedrockruntime.models.amazon.nova.text.Converse::converse),
                new ModelTest("TitanText", com.example.bedrockruntime.models.amazonTitanText.Converse::converse)
        );
    }
}