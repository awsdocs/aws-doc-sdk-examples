import { describe, expect, it } from "vitest";

import { main } from "../actions/query.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("query", () => {
  const tableName = "Pies";

  tableSetupTeardown(
    tableName,
    [
      {
        AttributeName: "Flavor",
        AttributeType: "S",
      },
      { AttributeName: "CrustType", AttributeType: "S" },
    ],
    [
      {
        Flavor: { S: "Key Lime" },
        CrustType: { S: "Graham Cracker" },
        Description: { S: "Contains no coloring. If you know, you know." },
      },
      {
        Flavor: { S: "Key Lime" },
        CrustType: { S: "Pastry" },
        Description: { S: "An inferior variant." },
      },
    ]
  );

  it("should return a list of pies matching the query", async () => {
    const response = await main();
    expect(response.Items.length).toBe(1);
    expect(response.Items[0].CrustType.S).toBe("Graham Cracker");
  });
});
