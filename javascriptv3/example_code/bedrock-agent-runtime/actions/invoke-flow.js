// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from "node:url";

import {
  BedrockAgentRuntimeClient,
  InvokeFlowCommand,
} from "@aws-sdk/client-bedrock-agent-runtime";

/**
 * Invokes an alias of a flow to run the inputs that you specify and return
 * the output of each node as a stream.
 *
 * @param {string} flowIdentifier - The unique identifier of the flow.
 * @param {string} flowAliasIdentifier - The unique identifier of the flow alias.
 * @param {string} prompt - The input to send to the prompt flow input node.
 * @param {string} [region='us-east-1'] - The AWS region in use.
 * @returns {Promise<import("@aws-sdk/client-bedrock-agent").FlowNodeOutput>} An object containing information about the output from flow invocation.
 */
export const invokeBedrockFlow = async (
  flowIdentifier,
  flowAliasIdentifier,
  prompt,
  region = "us-east-1",
) => {
  const client = new BedrockAgentRuntimeClient({ region });

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
      },
    ],
  });

  let flowResponse = {};
  const response = await client.send(command);

  for await (const chunkEvent of response.responseStream) {
    const { flowOutputEvent, flowCompletionEvent } = chunkEvent;

    if (flowOutputEvent) {
      flowResponse = { ...flowResponse, ...flowOutputEvent };
      console.log("Flow output event:", flowOutputEvent);
    } else if (flowCompletionEvent) {
      flowResponse = { ...flowResponse, ...flowCompletionEvent };
      console.log("Flow completion event:", flowCompletionEvent);
    }
  }

  return flowResponse;
};

// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const flowIdentifier = "[YOUR_FLOW_ID]";
  const flowAliasIdentifier = "[YOUR_FLOW_ALIAS_ID]";
  const prompt = "Hi, how are you?";

  const result = await invokeBedrockFlow(
    flowIdentifier,
    flowAliasIdentifier,
    prompt,
  );
  console.log("Final flow output: ", result);
}
