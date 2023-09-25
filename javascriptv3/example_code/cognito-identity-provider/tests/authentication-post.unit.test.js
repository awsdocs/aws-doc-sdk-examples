/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect } from "vitest";

import { handler } from "../scenarios/lambda-triggers/functions/authentication-post.mjs";

describe("authentication-post", () => {
  it("should return the same event it was passed", async () => {
    const result = await handler({
      triggerSource: "test",
      userPoolId: "testUserPoolId",
      callerContext: { clientId: "clientId" },
      userName: "Peccy",
    });
    expect(result).toEqual({
      triggerSource: "test",
      userPoolId: "testUserPoolId",
      callerContext: { clientId: "clientId" },
      userName: "Peccy",
    });
  });
});
