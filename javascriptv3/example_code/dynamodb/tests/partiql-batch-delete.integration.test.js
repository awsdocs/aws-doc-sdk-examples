import { describe, it, expect } from "vitest";
import { DynamoDBClient, ScanCommand } from "@aws-sdk/client-dynamodb";

import { main } from "../actions/partiql/partiql-batch-delete.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("partiql-batch-delete", () => {
  const tableName = "Flavors";
  const client = new DynamoDBClient({});

  tableSetupTeardown(
    tableName,
    [
      {
        AttributeName: "Name",
        AttributeType: "S",
      },
    ],
    [{ Name: { S: "Grape" } }, { Name: { S: "Strawberry" } }],
  );

  it("should delete all the contents of the database", async () => {
    const command = new ScanCommand({
      TableName: tableName,
      Select: "COUNT",
      ConsistentRead: true,
    });

    const before = await client.send(command);
    expect(before.Count).toBe(2);

    await main();

    const after = await client.send(command);
    expect(after.Count).toBe(0);
  });
});
