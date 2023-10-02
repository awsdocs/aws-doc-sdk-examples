/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect } from "vitest";
import { handler } from "../scenarios/lambda-triggers/functions/custom-message-sign-up.mjs";

describe("custom-message-sign-up", () => {
  it("should return an unmodified event when the triggerSource is not CustomMessage_SignUp", async () => {
    const result = await handler({
      request: { triggerSource: "" },
      response: {},
    });
    expect(result).toEqual({ request: { triggerSource: "" }, response: {} });
  });

  it("should return a custom message when the triggerSource is CustomMessage_SignUp", async () => {
    const result = await handler({
      triggerSource: "CustomMessage_SignUp",
      request: { usernameParameter: "Peccy", codeParameter: "123" },
      response: {},
    });
    expect(result).toEqual(
      expect.objectContaining({
        response: expect.objectContaining({
          emailMessage: `Thank you for signing up. Your confirmation code is 123.`,
        }),
      }),
    );
  });
});
