/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect } from "vitest";

import { handler } from "../scenarios/lambda-triggers/functions/migrate-user.mjs";

describe("migrate-user", () => {
  it("should return the unmodified event if the trigger source has no match", async () => {
    const result = await handler({ triggerSource: "Random" });
    expect(result).toEqual({ triggerSource: "Random" });
  });

  it("should migrate a user on authentication", async () => {
    const result = await handler({
      userName: "belladonna",
      request: { password: "Test123" },
      triggerSource: "UserMigration_Authentication",
      response: {},
    });
    expect(result).toEqual({
      userName: "belladonna",
      request: { password: "Test123" },
      triggerSource: "UserMigration_Authentication",
      response: {
        finalUserStatus: "CONFIRMED",
        messageAction: "SUPPRESS",
        userAttributes: {
          email_verified: "true",
          email: "bella@example.com",
        },
      },
    });
  });

  it("should migrate a user on forgot password", async () => {
    const result = await handler({
      userName: "belladonna",
      triggerSource: "UserMigration_ForgotPassword",
      response: {},
    });
    expect(result).toEqual({
      userName: "belladonna",
      triggerSource: "UserMigration_ForgotPassword",
      response: {
        messageAction: "SUPPRESS",
        userAttributes: {
          email_verified: "true",
          email: "bella@example.com",
        },
      },
    });
  });
});
