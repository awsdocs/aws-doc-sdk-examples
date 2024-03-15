// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from "url";

import { FoundationModels } from "../../tools/foundation_models.js";
import { defaultProvider } from "@aws-sdk/credential-provider-node";
import {
  BedrockRuntimeClient,
  InvokeModelCommand,
} from "@aws-sdk/client-bedrock-runtime";

/**
 * @typedef {Object} ResponseBody
 * @property {Object[]} results
 */

/**
 * Invokes an Amazon Titan Text generation model.
 *
 * @param {string} prompt - The input text prompt for the model to complete.
 * @param {string} [modelId] - The ID of the model to use. Defaults to "amazon.titan-text-express-v1".
 * @returns {Promise<string[]>} The inference response from the model.
 */
export const invokeModel = async (prompt, modelId) => {
  // Create a new Bedrock Runtime client instance.
  const client = new BedrockRuntimeClient({
    region: "us-east-1",
    credentialDefaultProvider: defaultProvider,
  });

  // Use the provided model ID or fallback to Titan Text G1 - Express if not provided.
  modelId = modelId || "amazon.titan-text-express-v1";

  // Prepare the payload for the model.
  const payload = {
    inputText: prompt,
    textGenerationConfig: {
      maxTokenCount: 4096,
      stopSequences: [],
      temperature: 0,
      topP: 1,
    },
  };

  // Invoke the model with the payload and wait for the response.
  const command = new InvokeModelCommand({
    contentType: "application/json",
    body: JSON.stringify(payload),
    modelId,
  });
  const apiResponse = await client.send(command);

  // Decode and return the response.
  const decodedResponseBody = new TextDecoder().decode(apiResponse.body);
  /** @type {ResponseBody} */
  const responseBody = JSON.parse(decodedResponseBody);
  return responseBody.results.map((result) => result.outputText);
};

// Invoke the function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const prompt =
    'Complete the following in one sentence: "Once upon a time..."';
  const modelId = FoundationModels.TITAN_TEXT_G1_EXPRESS.modelId;
  console.log(`Prompt: ${prompt}`);
  console.log(`Model ID: ${modelId}`);

  try {
    console.log("-".repeat(53));
    const responses = await invokeModel(prompt, modelId);
    responses.forEach((response) => console.log(response));
  } catch (err) {
    console.log(err);
  }
}
