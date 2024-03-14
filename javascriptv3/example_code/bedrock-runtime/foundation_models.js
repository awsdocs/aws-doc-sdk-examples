// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * @typedef {Object} Module
 * @property {Function} invokeModel
 * @property {Function} invokeTextCompletionsApi
 */

/**
 * @typedef {Object} FoundationModel
 * @property {string} modelId
 * @property {string} modelName
 * @property {function(): Promise<Module>} module
 * @property {function(module: Module): Function} invoker
 */

/**
 * @type {Object.<string, FoundationModel>}
 */
export const FoundationModels = Object.freeze({
    CLAUDE_3_HAIKU: {
        modelId: "anthropic.claude-3-haiku-20240307-v1:0",
        modelName: "Anthropic Claude 3 Haiku",
        module: () => import("./models/anthropic_claude/claude_3.js"),
        invoker: (module) => /** @type {ModuleExport} */ module.invokeModel,
    },
    CLAUDE_3_SONNET: {
        modelId: "anthropic.claude-3-sonnet-20240229-v1:0",
        modelName: "Anthropic Claude 3 Sonnet",
        module: () => import("./models/anthropic_claude/claude_3.js"),
        invoker: (module) => module.invokeModel,
    },
    CLAUDE_2_1: {
        modelId: "anthropic.claude-v2:1",
        modelName: "Anthropic Claude 2.1",
        module: () => import("./models/anthropic_claude/claude_2.js"),
        invoker: (module) => module.invokeTextCompletionsApi,
    },
    CLAUDE_2: {
        modelId: "anthropic.claude-v2",
        modelName: "Anthropic Claude 2.0",
        module: () => import("./models/anthropic_claude/claude_2.js"),
        invoker: (module) => module.invokeTextCompletionsApi,
    },
    CLAUDE_INSTANT: {
        modelId: "anthropic.claude-instant-v1",
        modelName: "Anthropic Claude Instant",
        module: () => import("./models/anthropic_claude/claude_instant_1.js"),
        invoker: (module) => module.invokeTextCompletionsApi,
    },
    JURASSIC2_MID: {
        modelId: "ai21.j2-mid-v1",
        modelName: "Jurassic-2 Mid",
        module: () => import("./models/ai21_labs_jurassic2/jurassic2.js"),
        invoker: (module) => module.invokeModel,
    },
    JURASSIC2_ULTRA: {
        modelId: "ai21.j2-ultra-v1",
        modelName: "Jurassic-2 Ultra",
        module: () => import("./models/ai21_labs_jurassic2/jurassic2.js"),
        invoker: (module) => module.invokeModel,
    },
    LLAMA2_CHAT_13B: {
        modelId: "meta.llama2-13b-chat-v1",
        modelName: "Llama 2 Chat 13B",
        module: () => import("./models/meta_llama2/llama2_chat.js"),
        invoker: (module) => module.invokeModel,
    },
    LLAMA2_CHAT_70B: {
        modelId: "meta.llama2-70b-chat-v1",
        modelName: "Llama 2 Chat 70B",
        module: () => import("./models/meta_llama2/llama2_chat.js"),
        invoker: (module) => module.invokeModel,
    },
    MISTRAL_7B: {
        modelId: "mistral.mistral-7b-instruct-v0:2",
        modelName: "Mistral 7B Instruct",
        module: () => import("./models/mistral_ai/mistral_7b.js"),
        invoker: (module) => module.invokeModel,
    },
    MIXTRAL_8X7B: {
        modelId: "mistral.mixtral-8x7b-instruct-v0:1",
        modelName: "Mixtral 8X7B Instruct",
        module: () => import("./models/mistral_ai/mixtral_8x7b.js"),
        invoker: (module) => module.invokeModel,
    },
    TITAN_TEXT_G1_EXPRESS: {
        modelId: "amazon.titan-text-express-v1",
        modelName: "Titan Text G1 - Express",
        module: () => import("./models/amazon_titan/titan_text.js"),
        invoker: (module) => module.invokeModel,
    },
    TITAN_TEXT_G1_LITE: {
        modelId: "amazon.titan-text-lite-v1",
        modelName: "Titan Text G1 - Lite",
        module: () => import("./models/amazon_titan/titan_text.js"),
        invoker: (module) => module.invokeModel,
    },
});
