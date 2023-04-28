import { describe, it, expect, vi } from "vitest";
import { main } from "../actions/describe-table.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("describe-table", () => {
  const tableName = "Pastries";

  tableSetupTeardown(tableName, [
    {
      AttributeName: "ButterLayerCount",
      AttributeType: "N",
    },
  ]);

  it("should log the table name", async () => {
    const consoleSpy = vi.spyOn(console, "log");

    await main();

    expect(consoleSpy).toHaveBeenCalledWith("TABLE NAME: Pastries");
  });
});
