// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestImageGeneration extends AbstractModelTest {
    @Override
    protected String getMethodName() {
        return "invokeModel";
    }

    @Override
    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new TestInvokeModel.ModelTest("NovaCanvas", com.example.bedrockruntime.models.amazon.nova.canvas.InvokeModel.class),
                new TestInvokeModel.ModelTest("StableDiffusion", com.example.bedrockruntime.models.stabilityAi.InvokeModel.class),
                new TestInvokeModel.ModelTest("TitanImage", com.example.bedrockruntime.models.amazonTitanText.InvokeModel.class)
        );
    }
}