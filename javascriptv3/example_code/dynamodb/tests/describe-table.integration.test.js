import { describe, it, expect, beforeAll, afterAll, vi } from "vitest";
import {
  CreateTableCommand,
  DynamoDBClient,
  waitUntilTableExists,
} from "@aws-sdk/client-dynamodb";
import { main } from "../actions/describe-table.js";
import { DeleteTableCommand } from "@aws-sdk/client-dynamodb";

describe("describe-table", () => {
  const client = new DynamoDBClient({});
  const tableName = "Pastries";

  beforeAll(async () => {
    const command = new CreateTableCommand({
      TableName: tableName,
      AttributeDefinitions: [
        { AttributeName: "ButterLayerCount", AttributeType: "N" },
      ],
      KeySchema: [
        {
          AttributeName: "ButterLayerCount",
          KeyType: "HASH",
        },
      ],
      ProvisionedThroughput: {
        ReadCapacityUnits: 1,
        WriteCapacityUnits: 1,
      },
    });

    await client.send(command);
    await waitUntilTableExists({ client }, { TableName: tableName });
  });

  afterAll(async () => {
    const command = new DeleteTableCommand({ TableName: tableName });
    await client.send(command);
  });

  it("should log the table name", async () => {
    const consoleSpy = vi.spyOn(console, "log");

    await main();

    expect(consoleSpy).toHaveBeenCalledWith("TABLE NAME: Pastries");
  });
});
