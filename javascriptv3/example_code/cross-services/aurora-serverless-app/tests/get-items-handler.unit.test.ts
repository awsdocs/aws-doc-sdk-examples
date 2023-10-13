import { describe, it, vi, expect } from "vitest";
import { NextFunction, Request, Response } from "express";
import QueryString from "qs";
import { getItemsHandler } from "../src/handlers/get-items-handler.js";

describe("getItemsHandler", () => {
  it(
    "should create a request handler that sends a response " +
      "with the result of the call to the SDK client",
    async () => {
      // eslint-disable-next-line
      const sendable = { send: async <R>() => ({ records: [] }) as R };
      // eslint-disable-next-line
      const handler = getItemsHandler.withClient({ rdsDataClient: sendable });
      const send: unknown = vi.fn();

      await handler(
        { query: { archived: "false" } as QueryString.ParsedQs } as Request,
        { send } as Response,
        vi.fn() as unknown as NextFunction,
      );

      expect(send).toHaveBeenCalledWith([]);
    },
  );
});
