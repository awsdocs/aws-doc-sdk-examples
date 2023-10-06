import { describe, it, expect } from "vitest";

import { main } from "../actions/partiql/partiql-get.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("partiql-get", () => {
  const tableName = "CloudTypes";

  tableSetupTeardown(
    tableName,
    [
      {
        AttributeName: "Name",
        AttributeType: "S",
      },
    ],
    [
      { Name: { S: "Nimbus" }, IsStorm: { BOOL: "false" } },
      { Name: { S: "Cumulonimbus" }, IsStorm: { BOOL: "true" } },
    ],
  );

  it("should get only non-storm clouds from the database", async () => {
    const response = await main();

    expect(response.Items.length).toBe(1);
    expect(response.Items[0].IsStorm).toBe(false);
  });
});
