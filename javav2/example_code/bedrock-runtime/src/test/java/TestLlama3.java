// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrockruntime.models.meta.llama3.InvokeModelQuickstart;
import com.example.bedrockruntime.models.meta.llama3.InvokeModelWithResponseStreamQuickstart;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Tag("IntegrationTest")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestLlama3 {

    @Test
    void InvokeModel() {
        assertDoesNotThrow(() -> InvokeModelQuickstart.main(null));
    }

    @Test
    void InvokeModelWithResponseStream() {
        assertDoesNotThrow(() -> InvokeModelWithResponseStreamQuickstart.main(null));
    }

}
