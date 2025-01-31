// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class TestInvokeModelWithResponseStream extends IntegrationTestBase {

    @Test
    void testTitanText() throws ExecutionException, InterruptedException {
        String result = com.example.bedrockruntime.models.amazonTitanText.InvokeModelWithResponseStream.invokeModelWithResponseStream();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testClaude() throws ExecutionException, InterruptedException {
        String result = com.example.bedrockruntime.models.anthropicClaude.InvokeModelWithResponseStream.invokeModelWithResponseStream();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testCohereCommand() throws ExecutionException, InterruptedException {
        String result = com.example.bedrockruntime.models.cohereCommand.Command_InvokeModelWithResponseStream.invokeModelWithResponseStream();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testCohereCommandR() throws ExecutionException, InterruptedException {
        String result = com.example.bedrockruntime.models.cohereCommand.Command_R_InvokeModelWithResponseStream.invokeModelWithResponseStream();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testLlama3() {
        String result = com.example.bedrockruntime.models.metaLlama.Llama3_InvokeModelWithResponseStream.invokeModelWithResponseStream();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testMistral() throws ExecutionException, InterruptedException {
        String result = com.example.bedrockruntime.models.mistral.InvokeModelWithResponseStream.invokeModelWithResponseStream();
        assertNotNullOrEmpty(result);
    }
}
