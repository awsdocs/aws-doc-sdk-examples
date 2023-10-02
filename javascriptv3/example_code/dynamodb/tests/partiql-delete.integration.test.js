import { describe, it, expect } from "vitest";
import { DynamoDBClient, ScanCommand } from "@aws-sdk/client-dynamodb";

import { main } from "../actions/partiql/partiql-delete.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("partiql-delete", () => {
  const tableName = "PaintColors";
  const client = new DynamoDBClient({});

  tableSetupTeardown(
    tableName,
    [
      {
        AttributeName: "Name",
        AttributeType: "S",
      },
    ],
    [{ Name: { S: "Purple" } }],
  );

  it("should delete all the contents of the database", async () => {
    const command = new ScanCommand({
      TableName: tableName,
      Select: "COUNT",
      ConsistentRead: true,
    });

    const before = await client.send(command);
    expect(before.Count).toBe(1);

    await main();

    const after = await client.send(command);
    expect(after.Count).toBe(0);
  });
});
