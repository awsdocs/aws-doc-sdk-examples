import { describe, it, expect } from "vitest";

import { main } from "../actions/batch-get-item.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("batch-get-item", () => {
  const tableName = "PageAnalytics";

  tableSetupTeardown(
    tableName,
    [{ AttributeName: "PageName", AttributeType: "S" }],
    [
      { PageName: { S: "Home" }, PageViews: { N: "10" } },
      { PageName: { S: "About" }, PageViews: { N: "2" } },
    ],
  );

  it("should return a list of items", async () => {
    const { Responses } = await main();
    const pageAnalytics = Responses["PageAnalytics"];
    expect(pageAnalytics).toEqual(
      expect.arrayContaining([
        { PageViews: { N: "10" }, PageName: { S: "Home" } },
        { PageViews: { N: "2" }, PageName: { S: "About" } },
      ]),
    );
  });
});
