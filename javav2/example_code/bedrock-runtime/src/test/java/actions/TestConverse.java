// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import org.junit.jupiter.api.Test;

public class TestConverse extends IntegrationTestBase {
    @Test
    void testJurassic2() {
        String result = com.example.bedrockruntime.models.ai21LabsJurassic2.Converse.converse();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testTitanText() {
        String result = com.example.bedrockruntime.models.amazonTitanText.Converse.converse();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testClaude() {
        String result = com.example.bedrockruntime.models.anthropicClaude.Converse.converse();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testCohereCommand() {
        String result = com.example.bedrockruntime.models.cohereCommand.Converse.converse();
        assertNotNullOrEmpty(result);
    }

    @Test
    void testMistral() {
        String result = com.example.bedrockruntime.models.mistral.Converse.converse();
        assertNotNullOrEmpty(result);
    }
}
