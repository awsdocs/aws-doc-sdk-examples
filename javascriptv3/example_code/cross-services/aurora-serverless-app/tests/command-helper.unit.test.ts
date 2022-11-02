import { describe, it, expect } from "@jest/globals";
import { buildStatementCommand } from "../src/statement-commands/command-helper.js";

describe("command-helper", () => {
  describe("buildStatementCommand", () => {
    it("should create an ExecuteStatementCommand with the provided SQL statement", () => {
      const sql = "select * from some_table";
      const command = buildStatementCommand(sql);
      expect(command.constructor.name).toBe("ExecuteStatementCommand");
      expect(command.input.sql).toBe(sql)
    });
  });
});
