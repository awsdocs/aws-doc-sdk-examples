import { describe, it, expect } from "vitest";

import { main } from "../actions/document-client/batch-get.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("batch-get-item", () => {
  const tableName = "Books";

  tableSetupTeardown(
    tableName,
    [{ AttributeName: "Title", AttributeType: "S" }],
    [
      { Title: { S: "How to AWS" }, PageCount: { N: "10" } },
      { Title: { S: "DynamoDB for DBAs" }, PageCount: { N: "2" } },
    ],
  );

  it("should return a list of items", async () => {
    const { Responses } = await main();
    const books = Responses["Books"];
    expect(books).toEqual(
      expect.arrayContaining([
        { PageCount: 10, Title: "How to AWS" },
        { PageCount: 2, Title: "DynamoDB for DBAs" },
      ]),
    );
  });
});
