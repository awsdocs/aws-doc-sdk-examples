// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestInvokeModel extends AbstractModelTest {
    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new ModelTest("Claude", com.example.bedrockruntime.models.anthropicClaude.InvokeModel::invokeModel),
                new ModelTest("CohereCommandR", com.example.bedrockruntime.models.cohereCommand.Command_R_InvokeModel::invokeModel),
                new ModelTest("Llama", com.example.bedrockruntime.models.metaLlama.Llama3_InvokeModel::invokeModel),
                new ModelTest("Mistral", com.example.bedrockruntime.models.mistral.InvokeModel::invokeModel)
        );
    }
}