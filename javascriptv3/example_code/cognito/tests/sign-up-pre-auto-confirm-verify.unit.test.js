/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect } from "vitest";

import { handler } from "../scenarios/lambda-triggers/functions/sign-up-pre-auto-confirm-verify.mjs";

describe("sign-up-pre-auto-confirm-verify", () => {
  it("should auto verify the user, their phone, and their email", async () => {
    const result = await handler({
      request: {
        userAttributes: {
          email: "test@example.com",
          phone_number: "555-555-5555",
        },
      },
      response: {},
    });
    expect(result).toEqual({
      request: {
        userAttributes: {
          email: "test@example.com",
          phone_number: "555-555-5555",
        },
      },
      response: {
        autoConfirmUser: true,
        autoVerifyEmail: true,
        autoVerifyPhone: true,
      },
    });
  });

  it("should not auto verify the phone and email if they are missing", async () => {
    const result = await handler({
      request: {
        userAttributes: {},
      },
      response: {},
    });
    expect(result).toEqual({
      request: {
        userAttributes: {},
      },
      response: {
        autoConfirmUser: true,
      },
    });
  });
});
