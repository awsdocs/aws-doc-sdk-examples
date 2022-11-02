import { describe, expect, it, jest } from "@jest/globals";
import { Request, Response } from "express";
import { errorCodes, validateDb } from "../src/middleware/validate-db.js";

describe("validate-db", () => {
  describe("generated handler", () => {
    it("should send a CREATE TABLE statement if a 1146 error code is returned", async () => {
      const send = jest.fn(async () => {
        throw new Error(errorCodes.TABLE_NOT_FOUND);
      }) as Sendable["send"];
      const sendable: Sendable = { send };
      const handler = validateDb.withClient({ rdsDataClient: sendable });

      await handler(
        {} as Request,
        { send: jest.fn() as unknown } as Response,
        jest.fn()
      );

      expect(
        (send as jest.Mock<Sendable["send"]>).mock.calls[1][0].input.sql
      ).toEqual(
        "\nCREATE TABLE items (iditem VARCHAR(45), description VARCHAR(400), guide VARCHAR(45), status VARCHAR(400), username VARCHAR(45), archived TINYINT(4));\n"
      );
    });

    it("should call next if no error is returned", async () => {
      const send = jest.fn(async () => {}) as Sendable["send"];
      const sendable: Sendable = { send };
      const next = jest.fn();
      const handler = validateDb.withClient({ rdsDataClient: sendable });

      await handler(
        {} as Request,
        { send: jest.fn() as unknown } as Response,
        next
      );

      expect(next).toHaveBeenCalled();
    });
  });
});
