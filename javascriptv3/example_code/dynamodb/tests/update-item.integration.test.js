import { describe, it, expect } from "vitest";
import { DynamoDBDocumentClient, GetCommand } from "@aws-sdk/lib-dynamodb";
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";

import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";
import { main } from "../actions/update-item.js";

describe("update-item", () => {
  const tableName = "IceCreams";

  tableSetupTeardown(
    tableName,
    [{ AttributeName: "Flavor", AttributeType: "S" }],
    [
      { Flavor: { S: "Vanilla" }, HasChunks: { BOOL: "true" } },
      {
        Flavor: { S: "Chocolate Chip Cookie Dough" },
        HasChunks: { BOOL: "true" },
      },
    ],
  );

  it("should change vanilla to not be chunky", async () => {
    const client = DynamoDBDocumentClient.from(new DynamoDBClient({}));
    const command = new GetCommand({
      TableName: tableName,
      Key: { Flavor: "Vanilla" },
    });

    const beforeResponse = await client.send(command);
    expect(beforeResponse.Item.HasChunks).toBe(true);

    await main();

    const afterResponse = await client.send(command);
    expect(afterResponse.Item.HasChunks).toBe(false);
  });
});
