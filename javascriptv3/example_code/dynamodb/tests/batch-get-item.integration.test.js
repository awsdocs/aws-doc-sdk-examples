import { describe, it, beforeAll, expect, afterAll } from "vitest";
import {
  DynamoDBClient,
  CreateTableCommand,
  DeleteTableCommand,
  waitUntilTableExists,
} from "@aws-sdk/client-dynamodb";
import {
  DynamoDBDocumentClient,
  BatchWriteCommand,
} from "@aws-sdk/lib-dynamodb";

import { main } from "../actions/batch-get-item.js";

describe("batch-get-item", () => {
  const client = new DynamoDBClient({});
  const docClient = DynamoDBDocumentClient.from(client);
  const tableName = "PageAnalytics";

  beforeAll(async () => {
    const createTableCommand = new CreateTableCommand({
      TableName: tableName,
      ProvisionedThroughput: {
        ReadCapacityUnits: 5,
        WriteCapacityUnits: 5,
      },
      AttributeDefinitions: [{ AttributeName: "PageName", AttributeType: "S" }],
      KeySchema: [
        {
          AttributeName: "PageName",
          KeyType: "HASH",
        },
      ],
    });
    await client.send(createTableCommand);
    await waitUntilTableExists({ client }, { TableName: tableName });

    const writeCommand = new BatchWriteCommand({
      RequestItems: {
        PageAnalytics: [
          {
            PutRequest: {
              Item: {
                PageName: "Home",
                PageViews: 10,
              },
            },
          },
          {
            PutRequest: {
              Item: {
                PageName: "About",
                PageViews: 2,
              },
            },
          },
        ],
      },
    });

    await docClient.send(writeCommand);
  });

  afterAll(async () => {
    const command = new DeleteTableCommand({
      TableName: tableName,
    });

    await client.send(command);
  });

  it("should return a list of items", async () => {
    const { Responses } = await main();
    const pageAnalytics = Responses["PageAnalytics"];
    expect(pageAnalytics).toEqual(expect.arrayContaining([
      { PageViews: { N: "10" }, PageName: { S: "Home" } },
      { PageViews: { N: "2" }, PageName: { S: "About" } },
    ]));
  });
});
