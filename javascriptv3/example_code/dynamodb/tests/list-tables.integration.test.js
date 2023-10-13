import { describe, it, expect } from "vitest";

import { main } from "../actions/list-tables.js";
import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("list-tables", () => {
  const tableName = getUniqueName("list-tables-test");

  tableSetupTeardown(tableName, [{ AttributeName: "Id", AttributeType: "N" }]);

  it("should list my table", async () => {
    const response = await main();

    expect(response.TableNames).toContain(tableName);
  });
});
