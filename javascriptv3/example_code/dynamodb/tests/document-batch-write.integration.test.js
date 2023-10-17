import { describe, it, expect } from "vitest";

import { main } from "../actions/document-client/batch-write.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";
import { DynamoDBClient, GetItemCommand } from "@aws-sdk/client-dynamodb";
import { retry } from "@aws-sdk-examples/libs/utils/util-timers.js";

describe("document-batch-write", () => {
  const tableName = "BatchWriteMoviesTable";
  const client = new DynamoDBClient({});

  tableSetupTeardown(tableName, [
    {
      AttributeName: "title",
      AttributeType: "S",
    },
    { AttributeName: "year", AttributeType: "N" },
  ]);

  it("should write 300 movies to the table", async () => {
    await main();

    const command = new GetItemCommand({
      TableName: tableName,
      Key: {
        title: { S: "Little Black Book" },
        year: { N: "2004" },
      },
    });
    await retry({ intervalInMs: 1000, maxRetries: 30 }, async () => {
      const { Item } = await client.send(command);
      expect(Item).toBeDefined();
    });
  });
});
