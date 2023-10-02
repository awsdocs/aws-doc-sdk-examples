/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect } from "vitest";

import { handler } from "../scenarios/lambda-triggers/functions/sign-up-pre-auto-confirm-domain.mjs";

describe("sign-up-pre-auto-confirm-domain", () => {
  it("should auto confirm if the domain matches the target", async () => {
    const result = await handler({
      request: { userAttributes: { email: "test@example.com" } },
      response: {},
    });
    expect(result).toEqual({
      request: { userAttributes: { email: "test@example.com" } },
      response: { autoConfirmUser: true },
    });
  });

  it("should not auto confirm if the domain does not matches the target", async () => {
    const result = await handler({
      request: { userAttributes: { email: "test@example.ca" } },
      response: {},
    });
    expect(result).toEqual({
      request: { userAttributes: { email: "test@example.ca" } },
      response: { autoConfirmUser: false },
    });
  });
});
