// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from "node:url";
import {
  BedrockRuntimeClient,
  ConverseCommand,
} from "@aws-sdk/client-bedrock-runtime";

// snippet-start:[javascript.v3.bedrock-runtime.Hello]
// Send a prompt to Amazon Bedrock using the Converse API.

const AWS_REGION = "us-east-1";

// Set the model ID, e.g., Claude 3 Haiku.
const MODEL_ID = "anthropic.claude-3-haiku-20240307-v1:0";
const PROMPT = "Hi. In a short paragraph, explain what you can do.";

const hello = async () => {
  console.log("=".repeat(35));
  console.log("Welcome to the Amazon Bedrock demo!");
  console.log("=".repeat(35));

  console.log("Model: Anthropic Claude 3 Haiku");
  console.log(`Prompt: ${PROMPT}\n`);
  console.log("Invoking model...\n");

  // Create a new Bedrock Runtime client instance.
  const client = new BedrockRuntimeClient({ region: AWS_REGION });

  // Create the command with the model ID, the user message, and a basic configuration.
  const command = new ConverseCommand({
    modelId: MODEL_ID,
    messages: [
      {
        role: "user",
        content: [{ text: PROMPT }],
      },
    ],
  });

  // Send the command to the model and wait for the response.
  const response = await client.send(command);

  // Extract and print the response text.
  const responseText = response.output.message.content[0].text;
  console.log(`Response: ${responseText}`);
};
// snippet-end:[javascript.v3.bedrock-runtime.Hello]

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  await hello();
}
