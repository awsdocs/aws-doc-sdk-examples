import { describe, it, expect } from "vitest";

import { main } from "../actions/document-client/get.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("document-get", () => {
  const tableName = "AngryAnimals";

  tableSetupTeardown(
    tableName,
    [
      {
        AttributeName: "CommonName",
        AttributeType: "S",
      },
    ],
    [
      {
        CommonName: { S: "Shoebill" },
      },
    ],
  );

  it("should return the requested item", async () => {
    const { Item } = await main();
    expect(Item.CommonName).toBe("Shoebill");
  });
});
