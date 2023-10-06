import { describe, it, expect } from "vitest";

import { main } from "../actions/partiql/partiql-batch-get.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("partiql-batch-get", () => {
  const tableName = "PepperMeasurements";

  tableSetupTeardown(
    tableName,
    [
      {
        AttributeName: "Unit",
        AttributeType: "S",
      },
    ],
    [{ Unit: { S: "Teaspoons" } }, { Unit: { S: "Grams" } }],
  );

  it("should get specific items from the database", async () => {
    const response = await main();

    expect(response.Responses.length).toBe(2);
    expect(response.Responses[0].Item.Unit).toBe("Teaspoons");
    expect(response.Responses[1].Item.Unit).toBe("Grams");
  });
});
