// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestInvokeModel extends AbstractModelTest {
    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new ModelTest("Claude", com.example.bedrockruntime.models.anthropicClaude.InvokeModel::invokeModel),
                new ModelTest("CohereCommand", com.example.bedrockruntime.models.cohereCommand.Command_InvokeModel::invokeModel),
                new ModelTest("CohereCommandR", com.example.bedrockruntime.models.cohereCommand.Command_R_InvokeModel::invokeModel),
                new ModelTest("Jurassic2", com.example.bedrockruntime.models.ai21LabsJurassic2.InvokeModel::invokeModel),
                new ModelTest("Llama", com.example.bedrockruntime.models.metaLlama.Llama3_InvokeModel::invokeModel),
                new ModelTest("Mistral", com.example.bedrockruntime.models.mistral.InvokeModel::invokeModel),
                new ModelTest("TitanText", com.example.bedrockruntime.models.amazonTitanText.InvokeModel::invokeModel),
                new ModelTest("TitanTextEmbeddings", com.example.bedrockruntime.models.amazonTitanText.InvokeModel::invokeModel)
        );
    }
}