// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, beforeAll } from "vitest";
import {
  CreateTableCommand,
  DynamoDBClient,
  waitUntilTableExists,
} from "@aws-sdk/client-dynamodb";
import { main } from "../actions/delete-table.js";
import { DescribeTableCommand } from "@aws-sdk/client-dynamodb";

describe("delete-table", () => {
  const tableName = "DecafCoffees";
  const client = new DynamoDBClient({});

  beforeAll(async () => {
    const createTableCommand = new CreateTableCommand({
      TableName: tableName,
      AttributeDefinitions: [
        {
          AttributeName: "Brand",
          AttributeType: "S",
        },
      ],
      KeySchema: [
        {
          AttributeName: "Brand",
          KeyType: "HASH",
        },
      ],
      BillingMode: "PAY_PER_REQUEST",
    });

    await client.send(createTableCommand);
    await waitUntilTableExists({ client }, { TableName: tableName });
  });

  it("should delete the table", async () => {
    await main();

    const describeTableCommand = new DescribeTableCommand({
      TableName: tableName,
    });
    const response = await client.send(describeTableCommand);
    expect(response.Table.TableStatus).toBe("DELETING");
  });
});
