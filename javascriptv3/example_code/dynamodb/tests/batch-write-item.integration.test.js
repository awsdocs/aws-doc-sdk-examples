import { describe, it, expect } from "vitest";
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, BatchGetCommand } from "@aws-sdk/lib-dynamodb";

import { main } from "../actions/batch-write-item.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("batch-get-item", () => {
  const client = new DynamoDBClient({});
  const docClient = DynamoDBDocumentClient.from(client);
  const tableName = "Coffees";

  tableSetupTeardown(tableName, [
    { AttributeName: "Name", AttributeType: "S" },
  ]);

  it("should insert items into the table", async () => {
    await main();
    const getCommand = new BatchGetCommand({
      RequestItems: {
        [tableName]: {
          Keys: [
            {
              Name: "Donkey Kick",
            },
            { Name: "Flora Ethiopia" },
          ],
        },
      },
    });

    const { Responses } = await docClient.send(getCommand);
    expect(Responses).toEqual({
      [tableName]: expect.arrayContaining([
        expect.objectContaining({
          Name: "Donkey Kick",
        }),
        expect.objectContaining({
          Name: "Flora Ethiopia",
        }),
      ]),
    });
  });
});
