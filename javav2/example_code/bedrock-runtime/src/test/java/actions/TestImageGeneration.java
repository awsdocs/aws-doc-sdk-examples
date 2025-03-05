// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import java.util.stream.Stream;

public class TestImageGeneration extends AbstractModelTest {
    protected Stream<ModelTest> modelProvider() {
        return Stream.of(
                new ModelTest("NovaCanvas", com.example.bedrockruntime.models.amazon.nova.canvas.InvokeModel::invokeModel),
                new ModelTest("StableDiffusion", com.example.bedrockruntime.models.stabilityAi.InvokeModel::invokeModel),
                new ModelTest("TitanImage", com.example.bedrockruntime.models.amazonTitanText.InvokeModel::invokeModel)
        );
    }
}