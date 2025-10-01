// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * @typedef {(prompt: string, modelId: string) => Promise<string>} Invoker
 *
 * @typedef {{ invokeModel: Invoker }} Module
 */

export const FoundationModels = Object.freeze({
  CLAUDE_3_HAIKU: {
    modelId: "anthropic.claude-3-haiku-20240307-v1:0",
    modelName: "Anthropic Claude 3 Haiku",
    module: () => import("../models/anthropicClaude/invoke_claude_3.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
  CLAUDE_3_SONNET: {
    modelId: "anthropic.claude-3-sonnet-20240229-v1:0",
    modelName: "Anthropic Claude 3 Sonnet",
    module: () => import("../models/anthropicClaude/invoke_claude_3.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
  MISTRAL_7B: {
    modelId: "mistral.mistral-7b-instruct-v0:2",
    modelName: "Mistral 7B Instruct",
    module: () => import("../models/mistral/invoke_mistral_7b.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
  MIXTRAL_8X7B: {
    modelId: "mistral.mixtral-8x7b-instruct-v0:1",
    modelName: "Mixtral 8X7B Instruct",
    module: () => import("../models/mistral/invoke_mixtral_8x7b.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
});
