import { describe, it, expect, vi } from "vitest";

import { main } from "../hello.js";
import { getUniqueName } from "libs/utils/util-string.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("hello test", () => {
  const tableName = getUniqueName("list-tables-test");

  tableSetupTeardown(tableName, [{ AttributeName: "Id", AttributeType: "N" }]);

  it("should list tables", async () => {
    const spy = vi.spyOn(console, "log");

    await main();

    expect(spy).toHaveBeenCalledWith(`${tableName}`);
  });
});