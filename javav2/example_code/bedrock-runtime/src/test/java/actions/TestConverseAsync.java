// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestConverseAsync extends AbstractModelTest {
    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new ModelTest("Claude", com.example.bedrockruntime.models.anthropicClaude.ConverseAsync::converseAsync),
                new ModelTest("Jurassic2", com.example.bedrockruntime.models.ai21LabsJurassic2.ConverseAsync::converseAsync),
                new ModelTest("NovaText", com.example.bedrockruntime.models.amazon.nova.text.ConverseAsync::converseAsync),
                new ModelTest("TitanText", com.example.bedrockruntime.models.amazonTitanText.ConverseAsync::converseAsync),
                new ModelTest("CohereCommand", com.example.bedrockruntime.models.cohereCommand.ConverseAsync::converseAsync),
                new ModelTest("Mistral", com.example.bedrockruntime.models.mistral.ConverseAsync::converseAsync)
        );
    }
}