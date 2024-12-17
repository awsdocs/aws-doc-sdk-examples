// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.bedrockagents.runtime;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;

import com.example.bedrockagents.runtime.InvokeFlow;

public class InvokeFlowTest {

    // Fill in with the Prompt Flow Id and Alias.
    String flowId = "";
    String flowAliasId = "";
    String inputText = "Is putting pineapple on pizza a good idea?";
    String[] args = {flowId, flowAliasId, inputText};

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    void assertInvokeFlowAnswer() {
        String response = InvokeFlow.invokeFlowString(args);
        assertNotNull(response);
        assertFalse(response.isEmpty());
        System.out.println("Test 1 passed.");
    }
}
