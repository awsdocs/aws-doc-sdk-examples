// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi } from "vitest";
import { invokeBedrockAgent } from "../actions/invoke-agent.js";

const mocks = vi.hoisted(() => ({
  clientSendResolve: () =>
    Promise.resolve({
      completion: [
        {
          chunk: {
            bytes: new Uint8Array([
              116, 101, 115, 116, 32, 99, 111, 109, 112, 108, 101, 116, 105,
              111, 110,
            ]),
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
    InvokeAgentCommand: vi.fn(),
  };
});

describe("invokeBedrockAgent", () => {
  it("should return an object with sessionId and completion", async () => {
    const prompt = "Test prompt";
    const sessionId = "123";
    mocks.send.mockImplementationOnce(mocks.clientSendResolve);

    const result = await invokeBedrockAgent(prompt, sessionId);

    expect(result).toEqual({
      sessionId: sessionId,
      completion: "test completion",
    });
  });

  it("should log errors", async () => {
    mocks.send.mockImplementationOnce(mocks.clientSendReject);
    const spy = vi.spyOn(console, "error");
    const prompt = "Test prompt";
    const sessionId = "123";

    await invokeBedrockAgent(prompt, sessionId);

    expect(spy).toHaveBeenCalledWith(
      expect.objectContaining({ message: "Mocked error" }),
    );
  });
});
