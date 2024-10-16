// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, vi, expect } from "vitest";
import type { NextFunction, Request, Response } from "express";
import type QueryString from "qs";
import { getItemsHandler } from "../src/handlers/get-items-handler.js";

describe("getItemsHandler", () => {
  it("should create a request handler that sends a response " +
    "with the result of the call to the SDK client", async () => {
    const sendable = { send: async <R>() => ({ records: [] }) as R };
    const handler = getItemsHandler.withClient({ rdsDataClient: sendable });
    const send: unknown = vi.fn();

    await handler(
      { query: { archived: "false" } as QueryString.ParsedQs } as Request,
      { send } as Response,
      vi.fn() as unknown as NextFunction,
    );

    expect(send).toHaveBeenCalledWith([]);
  });
});
