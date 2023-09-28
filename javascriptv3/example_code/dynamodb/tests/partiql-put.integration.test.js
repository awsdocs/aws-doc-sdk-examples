import { describe, it, expect } from "vitest";
import { ScanCommand, DynamoDBClient } from "@aws-sdk/client-dynamodb";

import { main } from "../actions/partiql/partiql-put.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("partiql-put", () => {
  const tableName = "Flowers";
  const client = new DynamoDBClient({});

  tableSetupTeardown(tableName, [
    {
      AttributeName: "Name",
      AttributeType: "S",
    },
  ]);

  it("should put a rose into the database", async () => {
    const command = new ScanCommand({
      TableName: tableName,
      ConsistentRead: true,
    });

    const before = await client.send(command);
    expect(before.Items.length).toBe(0);

    await main();

    const after = await client.send(command);
    expect(after.Items.length).toBe(1);
    expect(after.Items).toEqual(
      expect.arrayContaining([{ Name: { S: "Rose" } }]),
    );
  });
});
