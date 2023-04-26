import { describe, it, afterAll } from "vitest";

import {
  DeleteTableCommand,
  DynamoDBClient,
  waitUntilTableExists,
} from "@aws-sdk/client-dynamodb";

import { main } from "../actions/create-table.js";

describe("create-table", () => {
  const client = new DynamoDBClient({});

  afterAll(async () => {
    const command = new DeleteTableCommand({
      TableName: "EspressoDrinks",
    });
    await client.send(command);
  });

  it("should create a table", async () => {
    await main();
    await waitUntilTableExists({ client }, { TableName: "EspressoDrinks" });
  });
});
