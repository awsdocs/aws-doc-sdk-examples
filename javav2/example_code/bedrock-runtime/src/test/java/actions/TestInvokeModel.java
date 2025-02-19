// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestInvokeModel extends AbstractModelTest {
    protected String getMethodName() {
        return "invokeModel";
    }

    protected Stream<TestInvokeModel.ModelTest> modelProvider() {
        return Stream.of(
                new TestInvokeModel.ModelTest("Claude", com.example.bedrockruntime.models.anthropicClaude.InvokeModel.class),
                new TestInvokeModel.ModelTest("CohereCommand", com.example.bedrockruntime.models.cohereCommand.Command_InvokeModel.class),
                new TestInvokeModel.ModelTest("CohereCommandR", com.example.bedrockruntime.models.cohereCommand.Command_R_InvokeModel.class),
                new TestInvokeModel.ModelTest("Jurassic2", com.example.bedrockruntime.models.ai21LabsJurassic2.InvokeModel.class),
                new TestInvokeModel.ModelTest("Llama", com.example.bedrockruntime.models.metaLlama.Llama3_InvokeModel.class),
                new TestInvokeModel.ModelTest("Mistral", com.example.bedrockruntime.models.mistral.InvokeModel.class),
                new TestInvokeModel.ModelTest("TitanText", com.example.bedrockruntime.models.amazonTitanText.InvokeModel.class),
                new TestInvokeModel.ModelTest("TitanTextEmbeddings", com.example.bedrockruntime.models.amazonTitanText.InvokeModel.class)
        );
    }
}