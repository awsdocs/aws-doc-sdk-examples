import { describe, it, expect } from "vitest";

import { main } from "../actions/document-client/delete.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { GetCommand, DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";

describe("document-delete", () => {
  const tableName = "Sodas";
  const client = new DynamoDBClient({});
  const docClient = DynamoDBDocumentClient.from(client);

  tableSetupTeardown(
    tableName,
    [{ AttributeName: "Flavor", AttributeType: "S" }],
    [
      {
        Flavor: { S: "Cola" },
      },
    ],
  );

  it("should remove an item from a database", async () => {
    const getCommand = new GetCommand({
      TableName: tableName,
      Key: {
        Flavor: "Cola",
      },
    });

    const before = await docClient.send(getCommand);
    expect(before.Item).toBeDefined();

    await main();

    const result = await docClient.send(getCommand);
    expect(result.Item).toBeUndefined();
  });
});
