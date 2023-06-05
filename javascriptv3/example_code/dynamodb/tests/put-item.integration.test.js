import { describe, it, expect } from "vitest";

import { main } from "../actions/put-item.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";
import { DynamoDBClient, GetItemCommand } from "@aws-sdk/client-dynamodb";

describe("put-item", () => {
  const tableName = "Cookies";

  tableSetupTeardown(tableName, [
    {
      AttributeName: "Flavor",
      AttributeType: "S",
    },
  ]);

  it("should list my table", async () => {
    await main();

    const client = new DynamoDBClient({});
    const command = new GetItemCommand({
      TableName: tableName,
      Key: {
        Flavor: { S: "Chocolate Chip" },
      },
    });

    const { Item } = await client.send(command);
    expect(Item["Flavor"]["S"]).toBe("Chocolate Chip");
  });
});
