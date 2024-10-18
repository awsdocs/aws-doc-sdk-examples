// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, expect, it, vi } from "vitest";
import type { NextFunction, Request, Response } from "express";
import { errorCodes, validateDb } from "../src/middleware/validate-db.js";
import type { Sendable } from "../src/types/sendable.ts";

describe("validate-db", () => {
  describe("generated handler", () => {
    it("should send a CREATE TABLE statement if a 1146 error code is returned", async () => {
      const send = vi.fn<[{ input: { sql: string } }]>(() => {
        throw new Error(errorCodes.TABLE_NOT_FOUND);
      });
      const sendable: Sendable = { send };
      const handler = validateDb.withClient({ rdsDataClient: sendable });
      await handler(
        {} as Request,
        { send: vi.fn() as unknown } as Response,
        vi.fn() as unknown as NextFunction,
      );

      expect(send.mock?.calls.at(1)?.at(0).input.sql).toEqual(
        "\nCREATE TABLE items (iditem VARCHAR(45), description VARCHAR(400), guide VARCHAR(45), status VARCHAR(400), username VARCHAR(45), archived TINYINT(4));\n",
      );
    });

    it("should call next if no error is returned", async () => {
      const send = vi.fn(async () => {}) as Sendable["send"];
      const sendable: Sendable = { send };
      const next = vi.fn();
      const handler = validateDb.withClient({ rdsDataClient: sendable });

      await handler(
        {} as Request,
        { send: vi.fn() as unknown } as Response,
        next,
      );

      expect(next).toHaveBeenCalled();
    });
  });
});
