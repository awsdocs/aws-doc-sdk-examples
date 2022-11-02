import { describe, it, jest, expect } from "@jest/globals";
import { Request, Response } from "express";
import QueryString from "qs";
import { getItemsHandler } from "../src/handlers/get-items-handler.js";

describe("getItemsHandler", () => {
  it(
    "should create a request handler that sends a response " +
      "with the result of the call to the SDK client",
    async () => {
      const sendable = { send: async <R>() => ({ records: [] } as R) };
      const handler = getItemsHandler.withClient({ rdsDataClient: sendable });
      const send: unknown = jest.fn();

      await handler(
        { query: { archived: "false" } as QueryString.ParsedQs } as Request,
        { send } as Response,
        jest.fn()
      );

      expect(send).toHaveBeenCalledWith([]);
    }
  );
});
