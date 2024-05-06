//  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
//  SPDX-License-Identifier: Apache-2.0

package amazontitan;

import com.example.bedrockruntime.models.amazon.embeddings.text.V2InvokeModelQuickstart;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static com.example.bedrockruntime.models.amazon.embeddings.text.V2InvokeModelScenarios.invokeModel;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("IntegrationTest")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestTitanEmbeddingsV2 {
    @Test
    void Quickstart() {
        assertDoesNotThrow(() -> V2InvokeModelQuickstart.main(null));
    }

    @Test
    void InvokeModel() {
        var inputText = "A text input";
        var dimensions = 256;
        var normalize = true;
        var response = invokeModel(inputText, dimensions, normalize);
        assertFalse(response.getJSONArray("embedding").isEmpty());
    }
}
