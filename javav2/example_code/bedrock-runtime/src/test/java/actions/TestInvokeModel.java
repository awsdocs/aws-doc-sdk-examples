// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import org.junit.jupiter.api.Test;

public class TestInvokeModel extends IntegrationTestBase {
    @Test
    void testJurassic2() {
        String result = com.example.bedrockruntime.models.ai21LabsJurassic2.InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testTitanImage() {
        String result = com.example.bedrockruntime.models.amazonTitanImage.InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testTitanText() {
        String result = com.example.bedrockruntime.models.amazonTitanText.InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testTitanTextEmbeddings() {
        String result = com.example.bedrockruntime.models.amazonTitanTextEmbeddings.InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testClaude() {
        String result = com.example.bedrockruntime.models.anthropicClaude.InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testCohereCommand() {
        String result = com.example.bedrockruntime.models.cohereCommand.Command_InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testCohereCommandR() {
        String result = com.example.bedrockruntime.models.cohereCommand.Command_R_InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testLlama3() {
        String result = com.example.bedrockruntime.models.metaLlama.Llama3_InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testMistral() {
        String result = com.example.bedrockruntime.models.mistral.InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testStableDiffusion() {
        String result = com.example.bedrockruntime.models.stabilityAi.InvokeModel.invokeModel();
        assertNotNullOrEmpty(result);
    }
}
