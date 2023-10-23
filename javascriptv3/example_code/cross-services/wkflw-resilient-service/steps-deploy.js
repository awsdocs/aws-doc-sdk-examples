/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { join } from "node:path";
import { readFileSync } from "node:fs";

import {
  BatchWriteItemCommand,
  CreateTableCommand,
  DynamoDBClient,
  waitUntilTableExists,
} from "@aws-sdk/client-dynamodb";

import {
  ScenarioOutput,
  ScenarioInput,
  ScenarioAction,
} from "@aws-sdk-examples/libs/scenario/index.js";

import { MESSAGES, PATHS } from "./constants.js";

/**
 * @type {import('@aws-sdk-examples/libs/scenario.js').Step[]}
 */
export const deploySteps = [
  new ScenarioOutput("introduction", MESSAGES.introduction, { header: true }),
  new ScenarioInput("confirmDeployment", MESSAGES.confirmDeployment, {
    type: "confirm",
  }),
  new ScenarioAction(
    "handleConfirmDeployment",
    (c) => c.confirmDeployment === false && process.exit(),
  ),
  new ScenarioOutput("creatingTable", (c) =>
    MESSAGES.creatingTable.replace("${TABLE_NAME}", c.tableName),
  ),
  new ScenarioAction("createTable", async (c) => {
    const client = new DynamoDBClient({});
    await client.send(
      new CreateTableCommand({
        TableName: c.tableName,
        ProvisionedThroughput: {
          ReadCapacityUnits: 5,
          WriteCapacityUnits: 5,
        },
        AttributeDefinitions: [
          {
            AttributeName: "MediaType",
            AttributeType: "S",
          },
          {
            AttributeName: "ItemId",
            AttributeType: "N",
          },
        ],
        KeySchema: [
          {
            AttributeName: "MediaType",
            KeyType: "HASH",
          },
          {
            AttributeName: "ItemId",
            KeyType: "RANGE",
          },
        ],
      }),
    );
    await waitUntilTableExists({ client }, { TableName: c.tableName });
  }),
  new ScenarioOutput("createTableResult", (c) => {
    return MESSAGES.createdTable.replace("${TABLE_NAME}", c.tableName);
  }),
  new ScenarioOutput("populatingTable", (c) => {
    return MESSAGES.populatingTable.replace("${TABLE_NAME}", c.tableName);
  }),
  new ScenarioAction("populateTable", (c) => {
    const client = new DynamoDBClient({});
    /**
     * @type {{ default: import("@aws-sdk/client-dynamodb").PutRequest['Item'][] }}
     */
    const recommendations = JSON.parse(
      readFileSync(
        join(
          PATHS.projectRoot,
          "workflows/resilient_service/resources/recommendations.json",
        ),
      ),
    );

    return client.send(
      new BatchWriteItemCommand({
        RequestItems: {
          [c.tableName]: recommendations.map((i) => ({
            PutRequest: { Item: i },
          })),
        },
      }),
    );
  }),
  new ScenarioOutput("populateTableResult", (c) => {
    return MESSAGES.populatedTable.replace("${TABLE_NAME}", c.tableName);
  }),
];
