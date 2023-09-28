import { describe, it, expect } from "vitest";

import { main } from "../actions/document-client/query.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("document-query", () => {
  const tableName = "CoffeeCrop";

  tableSetupTeardown(
    tableName,
    [
      { AttributeName: "OriginCountry", AttributeType: "S" },
      { AttributeName: "RoastDate", AttributeType: "S" },
    ],
    [
      {
        OriginCountry: { S: "Ethiopia" },
        RoastDate: { S: "2023-01-02" },
      },
      {
        OriginCountry: { S: "Ethiopia" },
        RoastDate: { S: "2023-05-02" },
      },
    ],
  );

  it("should only return coffees roasted after 2023-05-01", async () => {
    const { Items } = await main();
    expect(Items.length).toBe(1);
    expect(Items[0].RoastDate).toBe("2023-05-02");
  });
});
