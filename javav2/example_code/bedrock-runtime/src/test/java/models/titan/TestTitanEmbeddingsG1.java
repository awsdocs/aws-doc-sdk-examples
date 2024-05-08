// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package models.titan;

import com.example.bedrockruntime.models.amazon.embeddings.G1InvokeModelQuickstart;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static com.example.bedrockruntime.models.amazon.embeddings.G1InvokeModelScenarios.invokeModel;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("IntegrationTest")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestTitanEmbeddingsG1 {
    @Test
    void Quickstart() {
        assertDoesNotThrow(() -> G1InvokeModelQuickstart.main(null));
    }

    @Test
    void InvokeModel() {
        var inputText = "A text input";
        var response = invokeModel(inputText);
        assertFalse(response.getJSONArray("embedding").isEmpty());
    }
}
