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
  CLAUDE_2_1: {
    modelId: "anthropic.claude-v2:1",
    modelName: "Anthropic Claude 2.1",
    module: () => import("../models/anthropicClaude/invoke_claude_2.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
  CLAUDE_2: {
    modelId: "anthropic.claude-v2",
    modelName: "Anthropic Claude 2.0",
    module: () => import("../models/anthropicClaude/invoke_claude_2.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
  CLAUDE_INSTANT: {
    modelId: "anthropic.claude-instant-v1",
    modelName: "Anthropic Claude Instant",
    module: () =>
      import("../models/anthropicClaude/invoke_claude_instant_1.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
  JURASSIC2_MID: {
    modelId: "ai21.j2-mid-v1",
    modelName: "Jurassic-2 Mid",
    module: () => import("../models/ai21LabsJurassic2/invoke_model.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
  JURASSIC2_ULTRA: {
    modelId: "ai21.j2-ultra-v1",
    modelName: "Jurassic-2 Ultra",
    module: () => import("../models/ai21LabsJurassic2/invoke_model.js"),
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
  TITAN_TEXT_G1_EXPRESS: {
    modelId: "amazon.titan-text-express-v1",
    modelName: "Titan Text G1 - Express",
    module: () => import("../models/amazonTitanText/invoke_model.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
  TITAN_TEXT_G1_LITE: {
    modelId: "amazon.titan-text-lite-v1",
    modelName: "Titan Text G1 - Lite",
    module: () => import("../models/amazonTitanText/invoke_model.js"),
    invoker: (/** @type {Module} */ module) => module.invokeModel,
  },
});
