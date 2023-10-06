/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect } from "vitest";
import { handler } from "../scenarios/lambda-triggers/functions/custom-message-admin-create.mjs";

describe("custom-message-admin-create", () => {
  it("should return an unmodified event when the triggerSource is not AdminCreateUser", async () => {
    const result = await handler({
      request: { triggerSource: "" },
      response: {},
    });
    expect(result).toEqual({ request: { triggerSource: "" }, response: {} });
  });

  it("should return a custom message when the triggerSource is AdminCreateUser", async () => {
    const result = await handler({
      triggerSource: "CustomMessage_AdminCreateUser",
      request: { usernameParameter: "Peccy", codeParameter: "123" },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({
        response: expect.objectContaining({
          emailMessage: `Welcome to the service. Your user name is Peccy. Your temporary password is 123`,
        }),
      }),
    );
  });
});
