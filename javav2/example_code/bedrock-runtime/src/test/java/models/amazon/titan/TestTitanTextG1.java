// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package models.amazon.titan;

import com.example.bedrockruntime.models.amazon.titan.embeddings.TextEmbeddingsG1Quickstart;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static com.example.bedrockruntime.models.amazon.titan.embeddings.TextEmbeddingsG1Scenarios.invokeModel;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("IntegrationTest")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestTitanTextG1 {
    @Test
    void Quickstart() {
        assertDoesNotThrow(() -> TextEmbeddingsG1Quickstart.main(null));
    }

    @Test
    void InvokeModel() {
        var inputText = "A text input";
        var response = invokeModel(inputText);
        assertFalse(response.getJSONArray("embedding").isEmpty());
    }
}
