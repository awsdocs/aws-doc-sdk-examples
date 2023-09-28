import { describe, it, expect } from "vitest";

import { main } from "../actions/get-item.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("get-item", () => {
  const tableName = "CafeTreats";

  tableSetupTeardown(
    tableName,
    [
      {
        AttributeName: "TreatId",
        AttributeType: "N",
      },
    ],
    [{ TreatId: { N: "101" } }],
  );

  it("should return the requested item", async () => {
    const { Item } = await main();
    expect(Item.TreatId.N).toEqual("101");
  });
});
