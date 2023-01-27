/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi, beforeEach } from "vitest";

const send = vi.fn();

vi.doMock("@aws-sdk/client-sts", async () => {
  const actual = await vi.importActual("@aws-sdk/client-sts");
  return {
    ...actual,
    STSClient: class {
      send = send;
    },
  };
});

import { main } from "../actions/assume-role.js";

console.log = vi.fn();
console.error = vi.fn();

describe("assume-role", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should log a successful call", async () => {
    send.mockResolvedValueOnce("response");
    await main();
    expect(console.log).toHaveBeenCalledWith("response");
  });

  it("should log an error if the call fails", async () => {
    send.mockRejectedValueOnce("error");
    await main();
    expect(console.error).toHaveBeenCalledWith("error");
  });
});
