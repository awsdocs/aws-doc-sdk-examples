import { describe, it, expect, beforeAll, afterAll } from "vitest";
import {
  CreateTableCommand,
  DeleteTableCommand,
  DynamoDBClient,
  waitUntilTableExists,
} from "@aws-sdk/client-dynamodb";
import {
  DynamoDBDocumentClient,
  PutCommand,
  GetCommand,
} from "@aws-sdk/lib-dynamodb";
import { main } from "../actions/delete-item.js";

describe("delete-item", () => {
  const client = new DynamoDBClient({});
  const docClient = DynamoDBDocumentClient.from(client);
  const tableName = "Drinks";

  beforeAll(async () => {
    const createTableCommand = new CreateTableCommand({
      TableName: tableName,
      AttributeDefinitions: [
        {
          AttributeName: "Name",
          AttributeType: "S",
        },
      ],
      KeySchema: [
        {
          AttributeName: "Name",
          KeyType: "HASH",
        },
      ],
      ProvisionedThroughput: {
        ReadCapacityUnits: 1,
        WriteCapacityUnits: 1,
      },
    });
    await client.send(createTableCommand);
    await waitUntilTableExists({ client }, { TableName: tableName });

    const putCommand = new PutCommand({
      TableName: tableName,
      Item: {
        Name: "Pumpkin Spice Latte",
      },
    });

    await docClient.send(putCommand);
  });

  afterAll(async () => {
    const deleteTableCommand = new DeleteTableCommand({
      TableName: tableName,
    });
    await client.send(deleteTableCommand);
  });

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
