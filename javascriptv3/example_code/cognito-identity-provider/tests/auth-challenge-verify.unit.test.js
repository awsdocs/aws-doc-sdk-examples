/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect } from "vitest";
import { handler } from "../scenarios/lambda-triggers/functions/auth-challenge-verify.mjs";

describe("auth-challenge-verify", () => {
  it("should indicate a correct answer in the response if the answer is correct", async () => {
    const result = await handler({
      request: {
        privateChallengeParameters: { answer: "5" },
        challengeAnswer: "5",
      },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({ response: { answerCorrect: true } }),
    );
  });

  it("should indicate an incorrect answer in the response if the answer is incorrect", async () => {
    const result = await handler({
      request: {
        privateChallengeParameters: { answer: "1" },
        challengeAnswer: "5",
      },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({ response: { answerCorrect: false } }),
    );
  });
});
