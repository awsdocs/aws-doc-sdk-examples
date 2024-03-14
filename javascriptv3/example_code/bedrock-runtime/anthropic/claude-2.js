// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from "url";

import {
  BedrockRuntimeClient,
  InvokeModelCommand,
} from "@aws-sdk/client-bedrock-runtime";
import {defaultProvider} from "@aws-sdk/credential-provider-node";

/**
 * Invokes Anthropic Claude 2.x using the Messages API.
 *
 * To learn more about the Anthropic Messages API, go to:
 * https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-anthropic-claude-messages.html
 *
 * @param {string} prompt - The input text prompt for Claude to complete.
 * @param {string} [modelId] - The ID of the Anthropic model to use. Defaults to "anthropic.claude-v2".
 * @returns {Promise<string>} The inference response from the model.
 */
export const invokeMessagesApi = async (prompt, modelId) => {
  // Create a new Bedrock Runtime client instance.
  const client = new BedrockRuntimeClient({
    region: "us-east-1",
    credentialDefaultProvider: defaultProvider,
  });

  // Use the provided model ID or fallback to Claude Instant v1 if not provided.
  modelId = modelId || "anthropic.claude-v2";

  // Prepare the payload for the Messages API request.
  const payload = {
    anthropic_version: "bedrock-2023-05-31",
    max_tokens: 1000,
    messages: [
      {
        role: "user",
        content: [
          { type: "text", "text": prompt}
        ]
      }
    ],
  };

  // Invoke Claude with the payload and wait for the response.
  const command = new InvokeModelCommand({
    contentType: "application/json",
    body: JSON.stringify(payload),
    modelId,
  });
  const apiResponse = await client.send(command);

  // Decode and print the response.
  const decoded = new TextDecoder().decode(apiResponse.body);
  return JSON.parse(decoded).content[0].text;
};

/**
 * Invokes Anthropic Claude 2.x using the Text Completions API.
 *
 * To learn more about the Anthropic Text Completions API, go to:
 * https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-anthropic-claude-text-completion.html
 *
 * @param {string} prompt - The input text prompt for Claude to complete.
 * @param {string} [modelId] - The ID of the Anthropic model to use. Defaults to "anthropic.claude-v2".
 * @returns {Promise<string>} The inference response from the model.
 */
export const invokeTextCompletionsApi = async (prompt, modelId) => {
  // Create a new Bedrock Runtime client instance.
  const client = new BedrockRuntimeClient({
    region: "us-east-1",
    credentialDefaultProvider: defaultProvider,
  });

  // Use the provided model ID or fallback to Claude 2.0 if not provided.
  modelId = modelId || "anthropic.claude-v2";

  // Prepare the payload for the Text Completions API, using the required prompt template.
  const enclosedPrompt = `Human: ${prompt}\n\nAssistant:`;
  const payload = {
    prompt: enclosedPrompt,
    max_tokens_to_sample: 500,
    temperature: 0.5,
    stop_sequences: ["\n\nHuman:"],
  };

  // Invoke Claude with the payload and wait for the response.
  const command = new InvokeModelCommand({
    contentType: "application/json",
    body: JSON.stringify(payload),
    modelId,
  });
  const apiResponse = await client.send(command);

  // Decode and print the response.
  const decoded = new TextDecoder().decode(apiResponse.body);
  return JSON.parse(decoded).completion;
};

// Invoke the function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const prompt = 'Complete the following: "Once upon a time..."';
  console.log("\nModel: Anthropic Claude v2");
  console.log(`Prompt: ${prompt}`);

  const completion = await invokeClaude(prompt);
  console.log("Completion:");
  console.log(completion);
  console.log("\n");
}
