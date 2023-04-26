import { describe, it, beforeAll, expect, afterAll } from "vitest";
import {
  DynamoDBClient,
  CreateTableCommand,
  DeleteTableCommand,
  waitUntilTableExists,
} from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, BatchGetCommand } from "@aws-sdk/lib-dynamodb";

import { main } from "../actions/batch-write-item.js";

describe("batch-get-item", () => {
  const client = new DynamoDBClient({});
  const docClient = DynamoDBDocumentClient.from(client);
  const tableName = "Coffees";

  beforeAll(async () => {
    const createTableCommand = new CreateTableCommand({
      TableName: tableName,
      ProvisionedThroughput: {
        ReadCapacityUnits: 5,
        WriteCapacityUnits: 5,
      },
      AttributeDefinitions: [{ AttributeName: "Name", AttributeType: "S" }],
      KeySchema: [
        {
          AttributeName: "Name",
          KeyType: "HASH",
        },
      ],
    });
    await client.send(createTableCommand);
    await waitUntilTableExists({ client }, { TableName: tableName });
  });

  afterAll(async () => {
    const command = new DeleteTableCommand({
      TableName: tableName,
    });

    await client.send(command);
  });

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
