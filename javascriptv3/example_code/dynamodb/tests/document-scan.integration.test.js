import { describe, it, expect, vi } from "vitest";

import { main } from "../actions/document-client/scan.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("document-scan", () => {
  const tableName = "Birds";

  tableSetupTeardown(
    tableName,
    [{ AttributeName: "Name", AttributeType: "S" }],
    [
      { Name: { S: "Owl" }, Color: { S: "Brown" }, AvgLifeSpan: { N: "13" } },
      { Name: { S: "Bluejay" }, Color: { S: "Blue" }, AvgLifeSpan: { N: "9" } },
      {
        Name: { S: "Parrot" },
        Color: { S: "Green" },
        AvgLifeSpan: { N: "1250" },
      },
    ],
  );

  it("should log all items in the table", async () => {
    const spy = vi.spyOn(console, "log");
    await main();

    expect(spy).toHaveBeenCalledWith(`Owl - (Brown, 13)`);
    expect(spy).toHaveBeenCalledWith(`Bluejay - (Blue, 9)`);
    expect(spy).toHaveBeenCalledWith(`Parrot - (Green, 1250)`);
  });
});
