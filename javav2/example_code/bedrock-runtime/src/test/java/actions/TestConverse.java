// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestConverse extends AbstractModelTest {
    protected String getMethodName() {
        return "converse";
    }

    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new ModelTest("Claude", com.example.bedrockruntime.models.anthropicClaude.Converse.class),
                new ModelTest("CohereCommand", com.example.bedrockruntime.models.cohereCommand.Converse.class),
                new ModelTest("Jurassic2", com.example.bedrockruntime.models.ai21LabsJurassic2.Converse.class),
                new ModelTest("Mistral", com.example.bedrockruntime.models.mistral.Converse.class),
                new ModelTest("NovaText", com.example.bedrockruntime.models.amazon.nova.text.Converse.class),
                new ModelTest("TitanText", com.example.bedrockruntime.models.amazonTitanText.Converse.class)
        );
    }
}