import { describe, it, expect } from "vitest";

import { main } from "../actions/document-client/put.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";
import { DynamoDBClient, GetItemCommand } from "@aws-sdk/client-dynamodb";

describe("document-put", () => {
  const tableName = "HappyAnimals";
  const client = new DynamoDBClient({});

  tableSetupTeardown(tableName, [
    {
      AttributeName: "CommonName",
      AttributeType: "S",
    },
  ]);

  it("should insert the requested item", async () => {
    await main();

    const command = new GetItemCommand({
      TableName: tableName,
      ConsistentRead: true,
      Key: {
        CommonName: { S: "Shiba Inu" },
      },
    });

    const { Item } = await client.send(command);
    expect(Item.CommonName.S).toBe("Shiba Inu");
  });
});
