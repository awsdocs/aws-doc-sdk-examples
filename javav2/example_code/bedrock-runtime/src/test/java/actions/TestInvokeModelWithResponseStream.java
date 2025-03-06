// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestInvokeModelWithResponseStream extends AbstractModelTest {
    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new ModelTest("Claude", com.example.bedrockruntime.models.anthropicClaude.InvokeModelWithResponseStream::invokeModelWithResponseStream),
                new ModelTest("CohereCommand", com.example.bedrockruntime.models.cohereCommand.Command_InvokeModelWithResponseStream::invokeModelWithResponseStream),
                new ModelTest("CohereCommandR", com.example.bedrockruntime.models.cohereCommand.Command_R_InvokeModelWithResponseStream::invokeModelWithResponseStream),
                new ModelTest("Llama", com.example.bedrockruntime.models.metaLlama.Llama3_InvokeModelWithResponseStream::invokeModelWithResponseStream),
                new ModelTest("Mistral", com.example.bedrockruntime.models.mistral.InvokeModelWithResponseStream::invokeModelWithResponseStream),
                new ModelTest("TitanText", com.example.bedrockruntime.models.amazonTitanText.InvokeModelWithResponseStream::invokeModelWithResponseStream)
        );
    }
}
