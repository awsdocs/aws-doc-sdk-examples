/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi, beforeAll } from "vitest";
let mockSendFn = vi.fn(async () => {});

vi.mock("@aws-sdk/client-ses", () => {
  return {
    SES: class {
      send = mockSendFn;
    },
    SendEmailCommand: class {},
  };
});

describe("confirmation-post", () => {
  let handler;

  beforeAll(async () => {
    const mod = await import(
      "../scenarios/lambda-triggers/functions/confirmation-post.mjs"
    );
    handler = mod.handler;
  });

  it("should attempt to send an email if the event has an email address", async () => {
    await handler({
      request: { userAttributes: { email: "mail@example.com" } },
    });

    expect(mockSendFn).toHaveBeenCalled();
  });
});
