// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestInvokeModelWithResponseStream extends AbstractModelTest {
    protected String getMethodName() {
        return "invokeModelWithResponseStream";
    }

    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new TestInvokeModel.ModelTest("Claude", com.example.bedrockruntime.models.anthropicClaude.InvokeModelWithResponseStream.class),
                new TestInvokeModel.ModelTest("CohereCommand", com.example.bedrockruntime.models.cohereCommand.Command_InvokeModelWithResponseStream.class),
                new TestInvokeModel.ModelTest("CohereCommandR", com.example.bedrockruntime.models.cohereCommand.Command_R_InvokeModelWithResponseStream.class),
                new TestInvokeModel.ModelTest("Llama", com.example.bedrockruntime.models.metaLlama.Llama3_InvokeModelWithResponseStream.class),
                new TestInvokeModel.ModelTest("Mistral", com.example.bedrockruntime.models.mistral.InvokeModelWithResponseStream.class),
                new TestInvokeModel.ModelTest("TitanText", com.example.bedrockruntime.models.amazonTitanText.InvokeModelWithResponseStream.class)
        );
    }
}
