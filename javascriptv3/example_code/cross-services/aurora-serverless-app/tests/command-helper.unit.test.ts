// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect } from "vitest";
import { buildStatementCommand } from "../src/statement-commands/command-helper.js";

describe("command-helper", () => {
  describe("buildStatementCommand", () => {
    it("should create an ExecuteStatementCommand with the provided SQL statement", () => {
      const sql = "select * from some_table";
      const command = buildStatementCommand(sql);
      expect(command.constructor.name).toBe("ExecuteStatementCommand");
      expect(command.input.sql).toBe(sql);
    });
  });
  it("should create an ExecuteStatementCommand with the provided SQL statement and parameters", () => {
    const sql = "select * from some_table where id = :id";
    const parameters = {
      id: { StringValue: "123" },
    };
    const command = buildStatementCommand(sql, parameters);
    expect(command.constructor.name).toBe("ExecuteStatementCommand");
    expect(command.input.sql).toBe(sql);
    expect(command.input.parameters).toEqual([parameters]);
  });
});
