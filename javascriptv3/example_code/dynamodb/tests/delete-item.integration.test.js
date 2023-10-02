import { describe, it, expect } from "vitest";
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, GetCommand } from "@aws-sdk/lib-dynamodb";
import { main } from "../actions/delete-item.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("delete-item", () => {
  const client = new DynamoDBClient({});
  const docClient = DynamoDBDocumentClient.from(client);
  const tableName = "Drinks";

  tableSetupTeardown(
    tableName,
    [{ AttributeName: "Name", AttributeType: "S" }],
    [{ Name: { S: "Pumpkin Spice Latte" } }],
  );

  it("should remove an item from a database", async () => {
    const getCommand = new GetCommand({
      TableName: tableName,
      Key: {
        Name: "Pumpkin Spice Latte",
      },
    });

    const before = await docClient.send(getCommand);
    expect(before.Item).toBeDefined();

    await main();

    const result = await docClient.send(getCommand);
    expect(result.Item).toBeUndefined();
  });
});
