// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi } from "vitest";
import { invokeBedrockFlow } from "../actions/invoke-flow.js";

const mocks = vi.hoisted(() => ({
  clientSendResolve: () =>
    Promise.resolve({
      responseStream: [
        {
          flowOutputEvent: {
            content: {
              document: "Test prompt",
            },
          },
        },
      ],
    }),
  clientSendReject: () => Promise.reject(new Error("Mocked error")),
  send: vi.fn(),
}));

vi.mock("@aws-sdk/client-bedrock-agent-runtime", () => {
  return {
    BedrockAgentRuntimeClient: vi.fn(() => ({ send: mocks.send })),
    InvokeFlowCommand: vi.fn(),
  };
});

describe("invokeBedrockFlow", () => {
  it("should return an object with responseStream", async () => {
    const prompt = "Test prompt";
    mocks.send.mockImplementationOnce(mocks.clientSendResolve);

    const result = await invokeBedrockFlow(prompt);

    expect(result).toEqual({
      content: {
        document: prompt,
      },
    });
  });
});
