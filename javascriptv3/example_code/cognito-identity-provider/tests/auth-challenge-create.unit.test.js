/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect } from "vitest";
import { handler } from "../scenarios/lambda-triggers/functions/auth-challenge-create.mjs";

describe("auth-challenge-create function", () => {
  it('should return the same request/response objects if the challenge name is not "CUSTOM_CHALLENGE"', async () => {
    const result = await handler({ request: {}, response: {} });
    expect(result).toEqual(
      expect.objectContaining({ request: {}, response: {} }),
    );
  });

  it("should return the same request/response objects if there are no sessions", async () => {
    const result = await handler({
      request: { challengeName: "CUSTOM_CHALLENGE", session: [] },
      response: {},
    });
    expect(result).toEqual({
      request: { challengeName: "CUSTOM_CHALLENGE", session: [] },
      response: {},
    });
  });

  it("should return a captcha challenge if the session length is 2", async () => {
    const result = await handler({
      request: { challengeName: "CUSTOM_CHALLENGE", session: [{}, {}] },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({
        response: {
          publicChallengeParameters: { captchaUrl: "url/123.jpg" },
          privateChallengeParameters: { answer: "5" },
        },
      }),
    );
  });

  it("should return a mascot challenge if the session length is 3", async () => {
    const result = await handler({
      request: { challengeName: "CUSTOM_CHALLENGE", session: [{}, {}, {}] },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({
        response: {
          publicChallengeParameters: {
            securityQuestion: "Who is your favorite team mascot?",
          },
          privateChallengeParameters: { answer: "Peccy" },
        },
      }),
    );
  });
});
