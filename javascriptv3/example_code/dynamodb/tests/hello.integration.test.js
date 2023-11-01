import { describe, it, expect } from "vitest";

import { main } from "../hello.js";
import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("hello test", () => {
  const tableName = getUniqueName("list-tables-test");

  tableSetupTeardown(tableName, [{ AttributeName: "Id", AttributeType: "N" }]);

  it("should list tables", async () => {
    const { TableNames } = await main();

    expect(TableNames.includes(tableName)).toBe(true);
  });
});
