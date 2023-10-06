/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect } from "vitest";
import { handler } from "../scenarios/lambda-triggers/functions/auth-challenge-define.mjs";

describe("auth-challenge-define", () => {
  it("should return a password verifier challenge if the challenge name is SRP_A", async () => {
    const result = await handler({
      response: {},
      request: { session: [{ challengeName: "SRP_A" }] },
    });
    expect(result).toEqual(
      expect.objectContaining({
        response: {
          issueTokens: false,
          failAuthentication: false,
          challengeName: "PASSWORD_VERIFIER",
        },
      }),
    );
  });

  it("should return a custom challenge if the password verifier challenge was successful", async () => {
    const result = await handler({
      request: {
        session: [
          {},
          { challengeName: "PASSWORD_VERIFIER", challengeResult: true },
        ],
      },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({
        response: {
          issueTokens: false,
          failAuthentication: false,
          challengeName: "CUSTOM_CHALLENGE",
        },
      }),
    );
  });

  it("should return a custom challenge if we're on the 4th challenge and the last challenge was successful", async () => {
    const result = await handler({
      request: {
        session: [
          {},
          {},
          { challengeName: "CUSTOM_CHALLENGE", challengeResult: true },
        ],
      },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({
        response: {
          issueTokens: false,
          failAuthentication: false,
          challengeName: "CUSTOM_CHALLENGE",
        },
      }),
    );
  });

  it("should return tokens if the last challenge was successful", async () => {
    const result = await handler({
      request: {
        session: [
          {},
          {},
          {},
          { challengeName: "CUSTOM_CHALLENGE", challengeResult: true },
        ],
      },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({
        response: {
          issueTokens: true,
          failAuthentication: false,
        },
      }),
    );
  });

  it("should fail authentication if there's no matching definition", async () => {
    const result = await handler({
      request: {
        session: [
          {},
          {},
          {},
          {},
          { challengeName: "CUSTOM_CHALLENGE", challengeResult: true },
        ],
      },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({
        response: {
          issueTokens: false,
          failAuthentication: true,
        },
      }),
    );
  });
});
