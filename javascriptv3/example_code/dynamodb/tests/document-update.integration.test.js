import { describe, it, expect } from "vitest";

import { main } from "../actions/document-client/update.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";
import { DynamoDBDocumentClient, GetCommand } from "@aws-sdk/lib-dynamodb";
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";

describe("document-update", () => {
  const tableName = "Dogs";
  const docClient = DynamoDBDocumentClient.from(new DynamoDBClient({}));

  tableSetupTeardown(
    tableName,
    [{ AttributeName: "Breed", AttributeType: "S" }],
    [{ Breed: { S: "Labrador" }, Color: { S: "chocolate" } }],
  );

  it("should change 'chocolate' to 'black'", async () => {
    const command = new GetCommand({
      TableName: tableName,
      Key: { Breed: "Labrador" },
      ConsistentRead: true,
    });

    const before = await docClient.send(command);
    expect(before.Item.Color).toBe("chocolate");

    await main();

    const after = await docClient.send(command);
    expect(after.Item.Color).toBe("black");
  });
});
