/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  BillingMode,
  CreateTableCommand,
  DeleteTableCommand,
  DynamoDBClient,
  waitUntilTableExists,
} from "@aws-sdk/client-dynamodb";

import { beforeAll, afterAll } from "vitest";
import { BatchWriteItemCommand } from "@aws-sdk/client-dynamodb";

/**
 *
 * @param {string} tableName
 * @param {import('@aws-sdk/client-dynamodb').AttributeDefinition[]} primaryKey
 * @param {Record<string, import('@aws-sdk/client-dynamodb').AttributeValue>[]} items
 */
export const tableSetupTeardown = (tableName, primaryKey, items = []) => {
  const client = new DynamoDBClient({});
  const createTableCommand = new CreateTableCommand({
    TableName: tableName,
    AttributeDefinitions: primaryKey,
    KeySchema: [
      {
        AttributeName: primaryKey[0].AttributeName,
        KeyType: "HASH",
      },
      ...(primaryKey[1]
        ? [
            {
              AttributeName: primaryKey[1].AttributeName,
              KeyType: "RANGE",
            },
          ]
        : []),
    ],
    BillingMode: BillingMode.PAY_PER_REQUEST,
  });
  const deleteTableCommand = new DeleteTableCommand({ TableName: tableName });

  beforeAll(async () => {
    await client.send(createTableCommand);
    await waitUntilTableExists({ client }, { TableName: tableName });

    if (items.length) {
      const batchWriteItemCommand = new BatchWriteItemCommand({
        RequestItems: {
          [tableName]: items.map((item) => ({
            PutRequest: {
              Item: item,
            },
          })),
        },
      });

      await client.send(batchWriteItemCommand);
    }
  });
  afterAll(() => client.send(deleteTableCommand));
};
