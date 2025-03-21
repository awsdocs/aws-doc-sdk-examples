// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.anthropicClaude.lib;

import software.amazon.awssdk.services.bedrockruntime.model.ReasoningContentBlock;

/**
 * Represents the dual-part response from Claude 3.7 with reasoning enabled.
 * Contains both the model's internal reasoning process and the final response text.
 *
 * @param reasoning The ReasoningContentBlock containing Claude's detailed thinking process
 * @param text      The final concise response generated after reasoning
 */
public record ReasoningResponse(ReasoningContentBlock reasoning, String text) {
}
