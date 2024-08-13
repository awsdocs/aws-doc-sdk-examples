// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  BedrockAgentRuntimeClient,
  InvokeFlowCommand,
} from "@aws-sdk/client-bedrock-agent-runtime";

/**
 * @typedef {Object} ResponseBody
 * @returns {Object} flowResponse
 */

/**
 * Invokes a Bedrock flow to run an inference using the input
 * provided in the request body.
 *
 * @param {string} prompt - The prompt that you want the Agent to complete.
 */
export const invokeBedrockFlow = async (prompt) => {
  const client = new BedrockAgentRuntimeClient({ region: "us-east-1" });
  // const client = new BedrockAgentRuntimeClient({
  //   region: "us-east-1",
  //   credentials: {
  //     accessKeyId: "accessKeyId", // permission to invoke flow
  //     secretAccessKey: "accessKeySecret",
  //   },
  // });

  const flowIdentifier = "AJBHXXILZN";
  const flowAliasIdentifier = "AVKP1ITZAA";

  const command = new InvokeFlowCommand({
    flowIdentifier,
    flowAliasIdentifier,
    inputs: [
      {
        content: {
          document: prompt,
        },
        nodeName: "FlowInputNode",
        nodeOutputName: "document",
      }
    ]
  });

  try {
    let flowResponse = {};
    const response = await client.send(command);

    if (response.responseStream === undefined) {
      throw new Error("responseStream is undefined");
    }

    for await (let chunkEvent of response.responseStream) {
      const { flowOutputEvent } = chunkEvent;
      flowResponse = { ...flowResponse, ...flowOutputEvent };
      console.log(flowOutputEvent);
    }

    return flowResponse;
  } catch (err) {
    console.error(err);
  }
};

// Call function if run directly
import { fileURLToPath } from "url";
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const result = await invokeBedrockFlow("I need help.");
  console.log(result);
}
