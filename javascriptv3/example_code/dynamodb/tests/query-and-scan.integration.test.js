import { describe, expect, it } from "vitest";

import { main as query } from "../actions/query.js";
import { main as scan } from "../actions/scan.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("query and scan", () => {
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
    ],
  );

  it("should return a list of pies matching the query/scan", async () => {
    const queryResponse = await query();
    expect(queryResponse.Items.length).toBe(1);
    expect(queryResponse.Items[0].CrustType.S).toBe("Graham Cracker");

    const scanResponse = await scan();
    expect(scanResponse.Items.length).toBe(1);
    expect(scanResponse.Items[0].CrustType.S).toBe("Graham Cracker");
  });
});
