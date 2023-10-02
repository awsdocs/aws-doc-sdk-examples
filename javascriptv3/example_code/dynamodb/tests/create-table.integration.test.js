import { describe, it, afterAll } from "vitest";

import {
  DeleteTableCommand,
  DynamoDBClient,
  waitUntilTableExists,
} from "@aws-sdk/client-dynamodb";

import { main } from "../actions/create-table.js";

describe("create-table", () => {
  const client = new DynamoDBClient({});
  const tableName = "EspressoDrinks";

  afterAll(async () => {
    const command = new DeleteTableCommand({
      TableName: tableName,
    });
    await client.send(command);
  });

  it("should create a table", async () => {
    await main();
    await waitUntilTableExists({ client }, { TableName: tableName });
  });
});
